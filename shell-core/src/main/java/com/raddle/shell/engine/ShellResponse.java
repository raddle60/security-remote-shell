package com.raddle.shell.engine;

/**
 * description: 
 * @author xurong
 * time : 2015年12月30日 下午6:12:52
 */
public interface ShellResponse {
    public static final int EXIT_UNKNOWN_ERROR = -99;
    public static final int EXIT_NOT_EXECUTED = -90;
    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_EXIT_CODE = 0;

    void writeResponse(int type, byte[] bytes);

    void writeMessage(String message);

    void writeExitCode(int code);

    void close();
}
