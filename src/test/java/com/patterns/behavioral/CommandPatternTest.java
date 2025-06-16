package com.patterns.behavioral;

import com.patterns.behavioral.command.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class CommandPatternTest {

    @Autowired
    private RemoteControl remoteControl;
    
    private Light light;
    private LightOnCommand lightOnCommand;
    private LightOffCommand lightOffCommand;

    @BeforeEach
    void setUp() {
        light = new Light();
        lightOnCommand = new LightOnCommand(light);
        lightOffCommand = new LightOffCommand(light);
    }

    @Test
    void testRemoteControlNotNull() {
        assertNotNull(remoteControl, "RemoteControl should be injected");
    }

    @Test
    void testLightInitialState() {
        assertFalse(light.isOn(), "Light should be initially off");
    }

    @Test
    void testLightOnCommand(CapturedOutput output) {
        lightOnCommand.execute();
        
        assertTrue(light.isOn(), "Light should be on after executing on command");
        assertTrue(output.getOut().contains("Light is ON"));
    }

    @Test
    void testLightOffCommand(CapturedOutput output) {
        light.turnOn(); // Turn on first
        lightOffCommand.execute();
        
        assertFalse(light.isOn(), "Light should be off after executing off command");
        assertTrue(output.getOut().contains("Light is OFF"));
    }

    @Test
    void testLightOnCommandUndo(CapturedOutput output) {
        lightOnCommand.execute();
        assertTrue(light.isOn(), "Light should be on");
        
        lightOnCommand.undo();
        assertFalse(light.isOn(), "Light should be off after undo");
        assertTrue(output.getOut().contains("Light is OFF"));
    }

    @Test
    void testLightOffCommandUndo(CapturedOutput output) {
        lightOffCommand.execute();
        assertFalse(light.isOn(), "Light should be off");
        
        lightOffCommand.undo();
        assertTrue(light.isOn(), "Light should be on after undo");
        assertTrue(output.getOut().contains("Light is ON"));
    }

    @Test
    void testRemoteControlExecuteCommand(CapturedOutput output) {
        remoteControl.executeCommand(lightOnCommand);
        
        assertTrue(light.isOn(), "Light should be on");
        assertTrue(output.getOut().contains("Light is ON"));
    }

    @Test
    void testRemoteControlUndoLastCommand(CapturedOutput output) {
        remoteControl.executeCommand(lightOnCommand);
        assertTrue(light.isOn(), "Light should be on");
        
        remoteControl.undoLastCommand();
        assertFalse(light.isOn(), "Light should be off after undo");
        assertTrue(output.getOut().contains("Light is OFF"));
    }

    @Test
    void testMultipleCommandsAndUndos(CapturedOutput output) {
        // Execute multiple commands
        remoteControl.executeCommand(lightOnCommand);
        remoteControl.executeCommand(lightOffCommand);
        remoteControl.executeCommand(lightOnCommand);
        
        assertTrue(light.isOn(), "Light should be on after last command");
        
        // Undo commands in reverse order
        remoteControl.undoLastCommand(); // Undo last on command
        assertFalse(light.isOn(), "Light should be off after first undo");
        
        remoteControl.undoLastCommand(); // Undo off command
        assertTrue(light.isOn(), "Light should be on after second undo");
        
        remoteControl.undoLastCommand(); // Undo first on command
        assertFalse(light.isOn(), "Light should be off after third undo");
    }

    @Test
    void testUndoWithoutCommands(CapturedOutput output) {
        // Try to undo when no commands have been executed
        assertDoesNotThrow(() -> remoteControl.undoLastCommand(), 
                          "Undo should not throw exception when no commands exist");
        
        assertFalse(light.isOn(), "Light state should remain unchanged");
    }

    @Test
    void testCommandHistory() {
        remoteControl.executeCommand(lightOnCommand);
        remoteControl.executeCommand(lightOffCommand);
        remoteControl.executeCommand(lightOnCommand);
        
        // Should be able to undo all three commands
        remoteControl.undoLastCommand();
        remoteControl.undoLastCommand();
        remoteControl.undoLastCommand();
        
        assertFalse(light.isOn(), "Light should be off after undoing all commands");
        
        // Fourth undo should not cause issues
        assertDoesNotThrow(() -> remoteControl.undoLastCommand());
    }

    @Test
    void testSameCommandMultipleTimes(CapturedOutput output) {
        // Execute same command multiple times
        remoteControl.executeCommand(lightOnCommand);
        remoteControl.executeCommand(lightOnCommand);
        remoteControl.executeCommand(lightOnCommand);
        
        assertTrue(light.isOn(), "Light should be on");
        
        // Undo should work for each execution
        remoteControl.undoLastCommand();
        assertFalse(light.isOn(), "Light should be off after first undo");
        
        remoteControl.undoLastCommand();
        assertTrue(light.isOn(), "Light should be on after second undo");
        
        remoteControl.undoLastCommand();
        assertFalse(light.isOn(), "Light should be off after third undo");
    }

    @Test
    void testAlternatingCommands(CapturedOutput output) {
        // Alternate between on and off commands
        remoteControl.executeCommand(lightOnCommand);
        assertTrue(light.isOn());
        
        remoteControl.executeCommand(lightOffCommand);
        assertFalse(light.isOn());
        
        remoteControl.executeCommand(lightOnCommand);
        assertTrue(light.isOn());
        
        remoteControl.executeCommand(lightOffCommand);
        assertFalse(light.isOn());
        
        // Undo in reverse order
        remoteControl.undoLastCommand(); // Undo off -> on
        assertTrue(light.isOn());
        
        remoteControl.undoLastCommand(); // Undo on -> off
        assertFalse(light.isOn());
        
        remoteControl.undoLastCommand(); // Undo off -> on
        assertTrue(light.isOn());
        
        remoteControl.undoLastCommand(); // Undo on -> off
        assertFalse(light.isOn());
    }

    @Test
    void testCommandWithNullLight() {
        assertThrows(NullPointerException.class, () -> {
            new LightOnCommand(null);
        }, "LightOnCommand should throw exception with null light");
        
        assertThrows(NullPointerException.class, () -> {
            new LightOffCommand(null);
        }, "LightOffCommand should throw exception with null light");
    }

    @Test
    void testLightDirectManipulation(CapturedOutput output) {
        // Test direct light manipulation vs command execution
        light.turnOn();
        assertTrue(light.isOn());
        
        // Execute off command
        remoteControl.executeCommand(lightOffCommand);
        assertFalse(light.isOn());
        
        // Undo should turn it back on
        remoteControl.undoLastCommand();
        assertTrue(light.isOn());
    }

    @Test
    void testCommandExecutionOutput(CapturedOutput output) {
        remoteControl.executeCommand(lightOnCommand);
        remoteControl.executeCommand(lightOffCommand);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Light is ON"));
        assertTrue(outputString.contains("Light is OFF"));
        
        // Check order of output
        int onIndex = outputString.indexOf("Light is ON");
        int offIndex = outputString.indexOf("Light is OFF");
        assertTrue(onIndex < offIndex, "ON should appear before OFF in output");
    }

    @Test
    void testUndoOutput(CapturedOutput output) {
        remoteControl.executeCommand(lightOnCommand);
        remoteControl.undoLastCommand();
        
        String outputString = output.getOut();
        
        // Should see both ON and OFF messages
        assertTrue(outputString.contains("Light is ON"));
        assertTrue(outputString.contains("Light is OFF"));
        
        // Count occurrences
        long onCount = outputString.lines()
                .filter(line -> line.contains("Light is ON"))
                .count();
        long offCount = outputString.lines()
                .filter(line -> line.contains("Light is OFF"))
                .count();
        
        assertEquals(1, onCount, "Should have 1 ON message");
        assertEquals(1, offCount, "Should have 1 OFF message");
    }

    @Test
    void testComplexCommandSequence(CapturedOutput output) {
        // Complex sequence: on, off, on, undo, undo, on
        remoteControl.executeCommand(lightOnCommand);   // Light: ON
        remoteControl.executeCommand(lightOffCommand);  // Light: OFF
        remoteControl.executeCommand(lightOnCommand);   // Light: ON
        
        assertTrue(light.isOn(), "Light should be on");
        
        remoteControl.undoLastCommand();  // Undo last on -> Light: OFF
        assertFalse(light.isOn(), "Light should be off");
        
        remoteControl.undoLastCommand();  // Undo off -> Light: ON
        assertTrue(light.isOn(), "Light should be on");
        
        remoteControl.executeCommand(lightOnCommand);   // Light: ON (no change)
        assertTrue(light.isOn(), "Light should still be on");
        
        String outputString = output.getOut();
        long onCount = outputString.lines()
                .filter(line -> line.contains("Light is ON"))
                .count();
        long offCount = outputString.lines()
                .filter(line -> line.contains("Light is OFF"))
                .count();
        
        assertEquals(4, onCount, "Should have 4 ON messages");
        assertEquals(2, offCount, "Should have 2 OFF messages");
    }
}
