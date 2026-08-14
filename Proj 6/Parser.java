import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser
 * Reads a .asm file, strips whitespace/comments, and exposes the
 * cleaned instructions one at a time. Also breaks each instruction
 * into its underlying components (symbol / dest / comp / jump).
 *
 * Supports reset() so the caller can rewind and do a two-pass assembly
 * (first pass for labels, second pass for code generation).
 */
public class Parser {

    public enum InstructionType {
        A_INSTRUCTION,
        C_INSTRUCTION,
        L_INSTRUCTION
    }

    private final List<String> lines;
    private int currentIndex;
    private String currentInstruction;

    /** Opens the input file/stream and gets ready to parse it. */
    public Parser(String inputFile) throws IOException {
        lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String rawLine;
            while ((rawLine = reader.readLine()) != null) {
                String cleaned = stripCommentsAndWhitespace(rawLine);
                if (!cleaned.isEmpty()) {
                    lines.add(cleaned);
                }
            }
        }

        currentIndex = -1;
        currentInstruction = null;
    }

    private String stripCommentsAndWhitespace(String line) {
        int commentIndex = line.indexOf("//");
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex);
        }
        // remove all internal whitespace as well (Hack instructions have none)
        return line.replaceAll("\\s+", "");
    }

    /** Are there more lines in the input? */
    public boolean hasMoreLines() {
        return currentIndex < lines.size() - 1;
    }

    /** Reads the next instruction and makes it the current instruction. */
    public void advance() {
        currentIndex++;
        currentInstruction = lines.get(currentIndex);
    }

    /** Rewinds the parser to the beginning, for a second pass. */
    public void reset() {
        currentIndex = -1;
        currentInstruction = null;
    }

    /** Returns the type of the current instruction. */
    public InstructionType instructionType() {
        if (currentInstruction.startsWith("@")) {
            return InstructionType.A_INSTRUCTION;
        } else if (currentInstruction.startsWith("(")) {
            return InstructionType.L_INSTRUCTION;
        } else {
            return InstructionType.C_INSTRUCTION;
        }
    }

    /**
     * Returns the symbol of the current instruction.
     * Valid only for A_INSTRUCTION (@xxx) or L_INSTRUCTION ((xxx)).
     */
    public String symbol() {
        InstructionType type = instructionType();
        if (type == InstructionType.A_INSTRUCTION) {
            return currentInstruction.substring(1);
        } else if (type == InstructionType.L_INSTRUCTION) {
            return currentInstruction.substring(1, currentInstruction.length() - 1);
        }
        return null;
    }

    /** Returns the symbolic dest part of the current C-instruction (may be ""). */
    public String dest() {
        if (currentInstruction.contains("=")) {
            return currentInstruction.split("=")[0];
        }
        return "";
    }

    /** Returns the symbolic comp part of the current C-instruction. */
    public String comp() {
        String instr = currentInstruction;
        if (instr.contains("=")) {
            instr = instr.substring(instr.indexOf('=') + 1);
        }
        if (instr.contains(";")) {
            instr = instr.substring(0, instr.indexOf(';'));
        }
        return instr;
    }

    /** Returns the symbolic jump part of the current C-instruction (may be ""). */
    public String jump() {
        if (currentInstruction.contains(";")) {
            return currentInstruction.substring(currentInstruction.indexOf(';') + 1);
        }
        return "";
    }
}