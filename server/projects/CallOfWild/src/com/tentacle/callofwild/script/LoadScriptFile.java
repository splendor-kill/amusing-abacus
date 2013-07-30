package com.tentacle.callofwild.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

/**
 * 任务脚本文件加载
 * @author sp.fu
 *
 */
public class LoadScriptFile {

	/**
	 * 遍历所有脚本
	 * @param scriptDir
	 * @throws ScriptException
	 * @throws IOException
	 */
    public static void loadScriptFile(String scriptDir) throws ScriptException, IOException {
        if (scriptDir == null || "".equals(scriptDir))
            return;

        ScriptContext context = new SimpleScriptContext();
        ScriptEngineManager sem = new ScriptEngineManager();
        JavaScriptEngineFactory jef = new JavaScriptEngineFactory();
        sem.registerEngineExtension("java", jef);
        ScriptEngine se = sem.getEngineByExtension("java");

        File scriptFile = new File(scriptDir);
        File[] files = scriptFile.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            context.setAttribute(ScriptEngine.FILENAME, file.getName(), ScriptContext.ENGINE_SCOPE);
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            se.eval(br, context);
            br.close();
        }

	}
}
