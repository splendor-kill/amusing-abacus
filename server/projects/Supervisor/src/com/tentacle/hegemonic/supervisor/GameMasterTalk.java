package com.tentacle.hegemonic.supervisor;

import static org.jboss.netty.channel.Channels.pipeline;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.tentacle.trickraft.protocol.MyCodec;
import com.tentacle.trickraft.protocol.MyCodec.Cocoon;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysCommonReq;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysGmTalk;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysGmTalkAns;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysOnliners;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysOnliners.SimpleInfo;
import com.tentacle.trickraft.protocol.ProtoAdmin.Warrant;
import com.tentacle.trickraft.protocol.ProtoBasis.Instruction;
import com.tentacle.trickraft.protocol.ProtoBasis.eChatObject;
import com.tentacle.trickraft.protocol.ProtoBasis.eCommand;
import com.tentacle.trickraft.protocol.ProtoPlayer.ChatData;



class MyHandler extends SimpleChannelUpstreamHandler {

    private GameMasterTalk controller;
    
    public MyHandler(GameMasterTalk controller) {
        this.controller = controller;
    }
    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        Channel ch = e.getChannel();
        System.err.println(e);
        ch.close();
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        System.out.println("some message received by " + Thread.currentThread() + ".");
        
        if (!(e.getMessage() instanceof Cocoon)) {
            System.out.println("it isnt my type");
            ctx.sendUpstream(e);
            return;
        }
            
        Cocoon cocoon = (Cocoon) e.getMessage();
        eCommand cmd = eCommand.valueOf(cocoon.cmdType);
        if (cmd == null) {
            System.out.println("what are you doing?");
            ctx.sendUpstream(e);
            return;
        }
//        System.out.println("accept cmd: " + cmd);
      
        try {
            switch (cmd) {
            case PUSH_CHAT_CONTENT:
                controller.recvChatData(ChatData.parseFrom(cocoon.dat));
                break;
            case SYS_GM_TALK:
                controller.recvGmTalkAns(SysGmTalkAns.parseFrom(cocoon.dat));
                break;
            case SYS_QUERY_ALLIES:
                controller.recvAllies(SysOnliners.parseFrom(cocoon.dat));
                break;
            case SYS_QUERY_ONLINERS:
                controller.recvOnliners(SysOnliners.parseFrom(cocoon.dat));
                break;
            default:
                ;
            }
        } catch (Exception ex) {
        }

    }
    
}




class Nexus {
    
    private GameMasterTalk controller;
    
    private ClientBootstrap bootstrap;
    private InetSocketAddress serverAddress;
    private Channel channel2Server;

    private String adminName, adminKey, gameMasterName;
    
    public Nexus(GameMasterTalk controller) {
        this.controller = controller;
        
    }
    
    public void init() throws FileNotFoundException, IOException {
        
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
       
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline p = pipeline();
                p.addLast("decoder", new MyCodec.MyDecoder());
                p.addLast("encoder", new MyCodec.MyEncoder());        
                p.addLast("handler", new MyHandler(Nexus.this.controller));
                return p;
            }
        });        
        
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        
        Properties p = new Properties();
        p.load(new FileInputStream(GameMasterTalk.CFG_FILE));
        
        String ipv4 = p.getProperty("game_server.ipv4", "127.0.0.1");
        String tmp = p.getProperty("game_server.listening_port", "57082");//DEFAult port
        int port = Integer.parseInt(tmp);
        adminName = p.getProperty("admin_name", "");
        adminKey = p.getProperty("admin_key", "");
        gameMasterName = p.getProperty("game_master_player_name", "");
        serverAddress = new InetSocketAddress(ipv4, port);
        
        
    }
    
    public boolean connectToServer() {
        final int timeout_in_ms = 1000 * 90;
        ChannelFuture future = bootstrap.connect(serverAddress);
        boolean isCompleted = future.awaitUninterruptibly(timeout_in_ms);
        if (isCompleted && future.isSuccess()) {
            channel2Server = future.getChannel();
            return true;
        }
        return false;
    }
    
    public Channel getChannelToServer() {
        if (channel2Server != null && channel2Server.isConnected())
            return channel2Server;
        if (channel2Server != null)
            channel2Server.close();
        boolean isOk = connectToServer();
        System.out.println("connect to server is[" + isOk + "]");
        return channel2Server;
    }
  
    public String getGameMasterName() {
        return gameMasterName;
    }
  
    public void join() throws IOException {
        Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_GM_TALK).setId(0l).build();
        SysGmTalk.Builder req = SysGmTalk.newBuilder().setCmd(ins)
                .setProof(Warrant.newBuilder().setAdminName(adminName).setCachet(adminKey)).setGameMaster(gameMasterName);
        Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());
        getChannelToServer().write(cocoon);
    }
        
    public void send(ChatData content) {
        System.out.println("send ["+content.getObject()+"] msg["+content.getMsg()+"] to ["+content.getTo()+"]");
        Instruction ins = Instruction.newBuilder().setCmd(eCommand.CHAT).setId(0l).build();
        Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), content.toByteArray());
        getChannelToServer().write(cocoon);
    }
    
    public void queryAllies() {
        Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_QUERY_ALLIES).setId(0l).build();
        SysCommonReq.Builder req = SysCommonReq.newBuilder().setCmd(ins)
                .setProof(Warrant.newBuilder().setAdminName(adminName).setCachet(adminKey));;
        Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());
        getChannelToServer().write(cocoon);
    }
    
    public void queryPlayers() {
        Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_QUERY_ONLINERS).setId(0l).build();
        SysCommonReq.Builder req = SysCommonReq.newBuilder().setCmd(ins)
                .setProof(Warrant.newBuilder().setAdminName(adminName).setCachet(adminKey));
        Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());
        getChannelToServer().write(cocoon);
    }
    
}


class MyModel {
    private static final int MAX_NUM_OF_CHAT_DATA = 100;
    
    private boolean isSave = false;
    private eChatObject chatScope = eChatObject.CHT_WORLD;
    private List<ChatData> chatHistory = new ArrayList<ChatData>(MAX_NUM_OF_CHAT_DATA);
    //id --> name of alliance
    public Map<Long, String> alliances = new HashMap<Long, String>();
    //id --> name of player
    public Map<Long, String> players = new HashMap<Long, String>();
    
    private String gameMasterName;
    private long gameMasterId;
    
    private long withId;
    
    private StringBuffer contentBuf = new StringBuffer();
//    private List<Integer> endIndexes = new ArrayList<Integer>();
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    private FileWriter fileWriter;

    public void setSave(boolean isSave) {
        this.isSave = isSave;
        if (!isSave) {
            dispose();
        }
    }
    public eChatObject getChatObject() {
        return chatScope;
    }
    public void setChatScope(eChatObject chatObject) {
        this.chatScope = chatObject;
    }
    public void addDat(ChatData dat) {
        while (!chatHistory.isEmpty() && chatHistory.size() >= MAX_NUM_OF_CHAT_DATA) {
            chatHistory.remove(0);
        }
        chatHistory.add(dat);
        if (isSave) {
            try {
                save(format(dat));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public String format() {
        contentBuf.delete(0, contentBuf.length());
        for (ChatData dat : chatHistory) {
            contentBuf.append(format(dat));
        }
        return contentBuf.toString();
    }
    
    private String format(ChatData dat) {
        String timeStr = dateFormat.format(new Date(dat.getTime()));
        return dat.getFromName() + ":  " + timeStr + "\n" + dat.getMsg() + "\n\n";
    }
    
    private void save(String msg) throws IOException {
        if (fileWriter == null) {
            fileWriter = new FileWriter(GameMasterTalk.CHAT_LOG_FILE, true);
        }
        if (fileWriter == null)
            return;
        fileWriter.write(msg);
        fileWriter.flush();
    }
    
    public void dispose() {
        if (fileWriter == null)
            return;
        try {
            fileWriter.close();
            fileWriter = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getGameMasterName() {
        return gameMasterName;
    }
    public void setGameMasterName(String gameMasterName) {
        this.gameMasterName = gameMasterName;
    }
    public long getGameMasterId() {
        return gameMasterId;
    }
    public void setGameMasterId(long gameMasterId) {
        this.gameMasterId = gameMasterId;
    }

    public long getWithId() {
        return withId;
    }
    
    public void setWithId(long withId) {
        this.withId = withId;
    }
    
    
}

public class GameMasterTalk {
    public final static String CFG_FILE = "res/config.properties";
    public final static String CHAT_LOG_FILE = "dat/chat.log";

    private JFrame frame;
    private JTextPane txtpnOutput;
    private JTextArea txtrInput;
    private JList<Object> nameList;
    private DefaultListModel<String> listModel;
    private Nexus nexus;
    private MyModel entity;
    
    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GameMasterTalk window = new GameMasterTalk();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public GameMasterTalk() {
        nexus = new Nexus(this);
       
        try {
            nexus.init();
            nexus.join();
        } catch (Exception e) {
        }

        entity = new MyModel();
        entity.setGameMasterName(nexus.getGameMasterName());
        initialize();
       
    }

    public void send(String msg) {
        if (msg.isEmpty())
            return;
        ChatData content = ChatData.newBuilder()
                .setObject(entity.getChatObject())
                .setFromName(entity.getGameMasterName())
                .setFrom(entity.getGameMasterId())
                .setTo(entity.getWithId())
                .setMsg(msg).setTime(System.currentTimeMillis()).build();
        entity.addDat(content);
        nexus.send(content);
        showAgain();
    }
    
    public void recvChatData(ChatData dat) {
        boolean add = true;
        if (entity.getChatObject() == eChatObject.CHT_PLAYER
                && dat.getFrom() != entity.getWithId()) {
            add = false;
        } else if (entity.getChatObject() == eChatObject.CHT_ALLIANCE
                && dat.getFrom() != entity.getWithId()) {
            add = false;
        }
        if (add) {
            entity.addDat(dat);
            
        }
        System.out.println("[" + dat.getFromName() + "]: " + dat.getMsg());
        showAgain();
    }
    
    public void recvGmTalkAns(SysGmTalkAns dat) {
        long gmId = dat.getGameMasterId();
        entity.setGameMasterId(gmId);
        System.out.println("gameMaster's id is ["+gmId+"]");
        
    }
    
    public void recvAllies(SysOnliners dat) {
        entity.alliances.clear();
        for (SimpleInfo p : dat.getInfoList()) {
            entity.alliances.put(p.getId(), p.getName());
        }
        System.out.println("there are ["+entity.alliances.size()+"] allies.");
        refreshList();
    }
    
    public void recvOnliners(SysOnliners dat) {
        entity.players.clear();
        for (SimpleInfo p : dat.getInfoList()) {
            entity.players.put(p.getId(), p.getName());
        }
        System.out.println("there are ["+entity.players.size()+"] players.");
        refreshList();
    }
    
    
    public void showAgain() {
        System.out.println(Thread.currentThread().getName());
        txtpnOutput.setText(entity.format());
    }
    
    public void refreshList() {
        listModel.clear();
        if (entity.getChatObject() == eChatObject.CHT_ALLIANCE) {
            for (String name : entity.alliances.values()) {
                listModel.addElement(name);
            }
        } else if (entity.getChatObject() == eChatObject.CHT_PLAYER) {
            for (String name : entity.players.values()) {
                listModel.addElement(name);
            }
        }
        
    }
    
    public void refreshList(Pattern pat) {
        if (pat == null)
            return;
        listModel.clear();
        if (entity.getChatObject() == eChatObject.CHT_ALLIANCE) {
            for (String name : entity.alliances.values()) {
                Matcher m = pat.matcher(name);
                if (m.find())
                    listModel.addElement(name);
            }
        } else if (entity.getChatObject() == eChatObject.CHT_PLAYER) {
            for (String name : entity.players.values()) {
                Matcher m = pat.matcher(name);
                if (m.find())
                    listModel.addElement(name);
            }
        }
    }
    
    
    
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 560, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {280, 130, 150, 0};
        gridBagLayout.rowHeights = new int[] {480, 40};
        gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, 1.0};
        frame.getContentPane().setLayout(gridBagLayout);
        
        JPanel panel_3 = new JPanel();
        panel_3.setBackground(UIManager.getColor("Panel.background"));
        GridBagConstraints gbc_panel_3 = new GridBagConstraints();
        gbc_panel_3.weighty = 1.0;
        gbc_panel_3.weightx = 0.6;
        gbc_panel_3.fill = GridBagConstraints.BOTH;
        gbc_panel_3.insets = new Insets(0, 0, 5, 5);
        gbc_panel_3.gridx = 0;
        gbc_panel_3.gridy = 0;
        frame.getContentPane().add(panel_3, gbc_panel_3);
        GridBagLayout gbl_panel_3 = new GridBagLayout();
        gbl_panel_3.columnWidths = new int[] {270};
        gbl_panel_3.rowHeights = new int[] {240, 130, 30, 0, 0, 0};
        gbl_panel_3.columnWeights = new double[]{1.0};
        gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        panel_3.setLayout(gbl_panel_3);
        
        
        txtpnOutput = new JTextPane();
        txtpnOutput.setEditable(false);
        txtpnOutput.setText("");
        
        JScrollPane scrollPane = new JScrollPane(txtpnOutput);
        GridBagConstraints gbc_txtpnOutput = new GridBagConstraints();
        gbc_txtpnOutput.weighty = 0.7;
        gbc_txtpnOutput.weightx = 1.0;
        gbc_txtpnOutput.fill = GridBagConstraints.BOTH;
        gbc_txtpnOutput.insets = new Insets(0, 0, 5, 0);
        gbc_txtpnOutput.gridx = 0;
        gbc_txtpnOutput.gridy = 0;
        panel_3.add(scrollPane, gbc_txtpnOutput);
        
        
        txtrInput = new JTextArea();
        GridBagConstraints gbc_txtrInput = new GridBagConstraints();
        gbc_txtrInput.gridheight = 2;
        gbc_txtrInput.weighty = 0.22;
        gbc_txtrInput.weightx = 1.0;
        gbc_txtrInput.fill = GridBagConstraints.BOTH;
        gbc_txtrInput.insets = new Insets(0, 0, 5, 0);
        gbc_txtrInput.gridx = 0;
        gbc_txtrInput.gridy = 1;
        panel_3.add(txtrInput, gbc_txtrInput);
        txtrInput.setText("");
       
        InputMap imap = txtrInput.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        imap.put(KeyStroke.getKeyStroke("ctrl ENTER"), "txt.send");
        Action sendAction = new SendAction();
        ActionMap amap = txtrInput.getActionMap();
        amap.put("txt.send", sendAction);
        
        JPanel panel_4 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        GridBagConstraints gbc_panel_4 = new GridBagConstraints();
        gbc_panel_4.insets = new Insets(0, 0, 5, 0);
        gbc_panel_4.weighty = 0.08;
        gbc_panel_4.weightx = 1.0;
        gbc_panel_4.fill = GridBagConstraints.BOTH;
        gbc_panel_4.gridx = 0;
        gbc_panel_4.gridy = 3;
        panel_3.add(panel_4, gbc_panel_4);        
        
        JButton btnSend = new JButton(sendAction);
        btnSend.setText("send");        
        panel_4.add(btnSend);
        

        
        JPanel panel_1 = new JPanel();
        panel_1.setBackground(UIManager.getColor("Panel.background"));
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.weighty = 1.0;
        gbc_panel_1.weightx = 0.16;
        gbc_panel_1.fill = GridBagConstraints.BOTH;
        gbc_panel_1.insets = new Insets(0, 0, 5, 5);
        gbc_panel_1.gridx = 1;
        gbc_panel_1.gridy = 0;
        frame.getContentPane().add(panel_1, gbc_panel_1);
        panel_1.setLayout(new GridLayout(0, 1, 0, 0));
        
        JPanel panel_5 = new JPanel();
        panel_1.add(panel_5);
        panel_5.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        final JCheckBox chckbxSave = new JCheckBox("save");
        chckbxSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                entity.setSave(chckbxSave.isSelected());
            }
        });
//        chckbxSave.setSelected(true);
        panel_5.add(chckbxSave);
        entity.setSave(chckbxSave.isSelected());
        
        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel.setBackground(Color.WHITE);
        panel_1.add(panel);
        
        
        JRadioButton rdbtnWorld = new JRadioButton("world");
        rdbtnWorld.setSelected(true);
        rdbtnWorld.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                entity.setChatScope(eChatObject.CHT_WORLD);
            }
        });
        JRadioButton rdbtnAlliance = new JRadioButton("alliance");
        rdbtnAlliance.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                entity.setChatScope(eChatObject.CHT_ALLIANCE);
                nexus.queryAllies();
            }
        });
        JRadioButton rdbtnPlayer = new JRadioButton("player");
        rdbtnPlayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                entity.setChatScope(eChatObject.CHT_PLAYER);
                nexus.queryPlayers();
            }
        });
        panel.setLayout(new GridLayout(0, 1, 0, 0));
        panel.add(rdbtnWorld);
        panel.add(rdbtnAlliance);
        panel.add(rdbtnPlayer);
        ButtonGroup group = new ButtonGroup();
        group.add(rdbtnWorld);
        group.add(rdbtnAlliance);
        group.add(rdbtnPlayer);
        
        JPanel panel_2 = new JPanel();
        panel_2.setBackground(UIManager.getColor("Panel.background"));
        GridBagConstraints gbc_panel_2 = new GridBagConstraints();
        gbc_panel_2.weighty = 1.0;
        gbc_panel_2.weightx = 0.24;
        gbc_panel_2.insets = new Insets(0, 0, 5, 0);
        gbc_panel_2.fill = GridBagConstraints.BOTH;
        gbc_panel_2.gridx = 2;
        gbc_panel_2.gridy = 0;
        frame.getContentPane().add(panel_2, gbc_panel_2);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[]{150, 0};
        gbl_panel_2.rowHeights = new int[] {48, 432, 0};
        gbl_panel_2.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        panel_2.setLayout(gbl_panel_2);
        
        final JTextField txtSearch = new JTextField();
        txtSearch.setText("");
        GridBagConstraints gbc_txtSearch = new GridBagConstraints();
        gbc_txtSearch.weightx = 1.0;
        gbc_txtSearch.weighty = 0.1;
        gbc_txtSearch.fill = GridBagConstraints.BOTH;
        gbc_txtSearch.insets = new Insets(0, 0, 5, 0);
        gbc_txtSearch.gridx = 0;
        gbc_txtSearch.gridy = 0;
        panel_2.add(txtSearch, gbc_txtSearch);
        txtSearch.setColumns(10);
        txtSearch.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String typing = txtSearch.getText();
                if (typing.isEmpty())
                    typing = ".";
                try {
                    Pattern pattern = Pattern.compile(typing);
                    refreshList(pattern);
                } catch (Exception ex) {
                    System.out.println("typing pattern incorrect.");
                }
            }
        });
        
        listModel = new DefaultListModel();
        nameList = new JList(listModel);
        nameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        ListSelectionModel listSelectionModel = nameList.getSelectionModel();
        listSelectionModel.addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting())
                            return;
                        int index = e.getFirstIndex();
                        Map<Long, String> map = null;
                        if (entity.getChatObject() == eChatObject.CHT_ALLIANCE)
                            map = entity.alliances;
                        else if (entity.getChatObject() == eChatObject.CHT_PLAYER)
                            map = entity.players;
                        if (map == null)
                            return;
                        String selected = listModel.get(index);
                        for (Entry<Long, String> i : map.entrySet()) {
                            if (i.getValue().equals(selected)) {
                                System.out.println("["+entity.getChatObject()+"] ["+i.getKey()+"] ["+i.getValue()+"] has been selected.");
                                entity.setWithId(i.getKey());
                            }
                        }
                        
                    }
                    
                });


        JScrollPane listScrollPane = new JScrollPane(nameList);
        
       
        GridBagConstraints gbc_list = new GridBagConstraints();
        gbc_list.weighty = 0.1;
        gbc_list.weightx = 1.0;
        gbc_list.fill = GridBagConstraints.BOTH;
        gbc_list.gridx = 0;
        gbc_list.gridy = 1;
        panel_2.add(listScrollPane, gbc_list);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                entity.dispose();
            }
        });
        
        frame.pack();
        txtrInput.requestFocusInWindow();

    }
    
    public class SendAction extends AbstractAction {
        {
            putValue(Action.NAME, "send");
        }

        public void actionPerformed(ActionEvent event) {
            try {
                send(txtrInput.getText());
            } catch (Exception e) {
            }
            txtrInput.setText("");
        }
    }
    

    
    
}
