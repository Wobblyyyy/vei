package vei;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("FieldCanBeLocal")
public class VeiWindow {
    private static final DefaultTerminalFactory factory =
            new DefaultTerminalFactory();
    private Terminal terminal;
    private Screen screen;
    private TextEditor editor;
    TextGraphics graphics;
    public final AtomicInteger commandRow = new AtomicInteger(0);
    public final AtomicInteger modeRow = new AtomicInteger(0);
    public final AtomicReference<TerminalSize> size = new AtomicReference<>();

    public VeiWindow() {

    }

    public void start() throws IOException {
        terminal = factory.createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.flush();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);
        editor = new TextEditor();
        graphics = screen.newTextGraphics();
    }

    public void loop() throws IOException {
        KeyStroke stroke;
        Key key;
        size.set(terminal.getTerminalSize());
        commandRow.set(size.get().getRows() - 1);
        modeRow.set(size.get().getRows() - 2);
        terminal.addResizeListener((terminal, newSize) -> {
            size.set(newSize);
            commandRow.set(newSize.getRows() - 1);
            modeRow.set(newSize.getRows() - 2);
            screen.clear();
        });
        editor.setText(
                """
                        public class TestClass {
                            public int testMethod1() {
                                return 69;
                            }
                            
                            public boolean testMethod2() {
                                return true;
                            }
                            public void onKeyDown(Key key) {
                                switch (mode) {
                                    case NORMAL:
                                        if (key == Key.ENTER) {
                                            executeCommand();
                                        } else {
                                            currentCommand[currentCommandIndex] = key;
                                            currentCommandIndex += 1;
                                            commandBuffer.append(Key.reverseMatch(key));
                                            tryExactMatch(normalRegistry);
                                        }
                                        break;
                                    case INSERT:
                                        currentCommand[currentCommandIndex] = key;
                                        currentCommandIndex += 1;
                                        commandBuffer.append(Key.reverseMatch(key));
                                        if (insertRegistry.anyMatches(
                                                currentCommand,
                                                currentCommandIndex
                                        )) {
                                            tryExactMatch(insertRegistry);
                                        } else {
                                            resetCommand();
                                        }
                                        break;
                                    case VISUAL:
                                        break;
                                }
                            }
                        }
                        """
        );
        int lineCount = editor.getLineCount();
        int startColumn = String.valueOf(lineCount).length() + 1;
        int minRow = 0;
        int maxRow = modeRow.get() - 1;
        while (true) {
            stroke = terminal.readInput();
            if (stroke.getKeyType() == KeyType.Escape) break;
            key = Key.match(
                    stroke.getCharacter(),
                    stroke.isCtrlDown(),
                    false
            );
            screen.doResizeIfNecessary();
            editor.onKeyDown(key);
            graphics.putString(
                    0,
                    commandRow.get(),
                    String.format(
                            "%s                                         ",
                            editor.getCommandArrayString()
                    )
            );
            graphics.putString(
                    0,
                    modeRow.get(),
                    editor.getEditorMode().toString()
            );
            TerminalPosition cursorPosition = editor.getCursorPosition(minRow, startColumn);
            graphics.putString(
                    size.get().getColumns() / 2,
                    modeRow.get(),
                    String.format(
                            "row: %s col: %s (%s)                             ",
                            cursorPosition.getRow() + 1,
                            cursorPosition.getColumn() - startColumn + 1,
                            editor.getCol()
                    )
            );
            String[] lines = editor.getLineStrings(0, editor.getLineCount());
            for (int i = 0; i < lines.length && i < modeRow.get(); i++) {
                graphics.putString(
                        0,
                        i,
                        String.valueOf(i + 1)
                );
                graphics.putString(
                        startColumn,
                        i,
                        lines[i]
                );
            }
            screen.setCursorPosition(cursorPosition);
            screen.refresh();
            Thread.yield();
        }
    }

    public void stop() throws IOException {
        terminal.exitPrivateMode();
        screen.close();
        terminal.close();
    }
}
