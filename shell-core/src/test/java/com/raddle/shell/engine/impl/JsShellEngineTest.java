package com.raddle.shell.engine.impl;

import org.junit.BeforeClass;
import org.junit.Test;

public class JsShellEngineTest {
    private static JsShellEngine engine = new JsShellEngine();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testExecuteShell() {
        engine.executeShell(null, null);
    }

}
