import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * HackAssembler
 * Drives the translation of a symbolic Hack assembly program (Prog.asm)
 * into binary Hack machine code (Prog.hack).
 *
 * Usage:
 *   java HackAssembler Prog.asm
 *
 * Produces Prog.hack in the same location as Prog.asm.
 *
 * Algorithm (two-pass):
 *   1) Initialize the symbol table with the predefined symbols.
 *   2) First pass: scan the whole program, recording (label, ROM address)
 *      for every (LABEL) declaration, without generating code.
 *   3) Second pass: scan the program again, translating every
 *      A-instruction and C-instruction into binary, resolving variables
 *      on the fly (bound to consecutive addresses starting at 16).
 */
public class HackAssembler {

    private static final int VARIABLE_START_ADDRESS = 16;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HackAssembler Prog.asm");
            return;
        }

        String inputFile = args[0];
        if (!inputFile.endsWith(".asm")) {
            System.out.println("Error: input file must have a .asm extension");
            return;
        }
        String outputFile = inputFile.substring(0, inputFile.length() - 4) + ".hack";

        try {
            SymbolTable symbolTable = new SymbolTable();

            firstPass(inputFile, symbolTable);
            secondPass(inputFile, outputFile, symbolTable);

            System.out.println("Assembly successful: " + outputFile);

        } catch (IOException e) {
            System.out.println("Error reading/writing file: " + e.getMessage());
        }
    }

    /** First pass: builds the label part of the symbol table (no code generated). */
    private static void firstPass(String inputFile, SymbolTable symbolTable) throws IOException {
        Parser parser = new Parser(inputFile);
        int romAddress = 0;

        while (parser.hasMoreLines()) {
            parser.advance();
            if (parser.instructionType() == Parser.InstructionType.L_INSTRUCTION) {
                String label = parser.symbol();
                if (!symbolTable.contains(label)) {
                    symbolTable.addEntry(label, romAddress);
                }
                // labels do not occupy a ROM slot, so romAddress is not incremented
            } else {
                romAddress++;
            }
        }
    }

    /** Second pass: generates the binary code, resolving variables along the way. */
    private static void secondPass(String inputFile, String outputFile, SymbolTable symbolTable)
            throws IOException {

        Parser parser = new Parser(inputFile);
        int nextVariableAddress = VARIABLE_START_ADDRESS;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            while (parser.hasMoreLines()) {
                parser.advance();
                Parser.InstructionType type = parser.instructionType();

                if (type == Parser.InstructionType.A_INSTRUCTION) {
                    String symbol = parser.symbol();
                    int address;

                    if (isNumeric(symbol)) {
                        address = Integer.parseInt(symbol);
                    } else {
                        if (!symbolTable.contains(symbol)) {
                            symbolTable.addEntry(symbol, nextVariableAddress);
                            nextVariableAddress++;
                        }
                        address = symbolTable.getAddress(symbol);
                    }

                    writer.write(to16BitBinary(address));
                    writer.newLine();

                } else if (type == Parser.InstructionType.C_INSTRUCTION) {
                    String dest = Code.dest(parser.dest());
                    String comp = Code.comp(parser.comp());
                    String jump = Code.jump(parser.jump());

                    writer.write("111" + comp + dest + jump);
                    writer.newLine();
                }
                // L_INSTRUCTION: nothing to write, already handled in the first pass
            }
        }
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }

    private static String to16BitBinary(int value) {
        StringBuilder binary = new StringBuilder(Integer.toBinaryString(value));
        while (binary.length() < 16) {
            binary.insert(0, "0");
        }
        return binary.toString();
    }
}