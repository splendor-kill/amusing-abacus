package com.tentacle.common.domain.baseinfo;

import org.apache.log4j.Logger;

public class PlayerInfo implements Cloneable {
	private static final Logger logger = Logger.getLogger(PlayerInfo.class);
	
	private long id;
	private int usersId;
	private String name;
	

		

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUsersId() {
		return usersId;
	}

	public void setUsersId(int l) {
		this.usersId = l;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}






    @Override
    public Object clone() {
        PlayerInfo replica = null;        
        try {
            replica = (PlayerInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
  
        // other fields just keep shadow copy
        return replica;
    }
    
}
