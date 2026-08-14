/**
 * Code
 * Translates each symbolic C-instruction field (dest, comp, jump)
 * into its corresponding binary value, according to the Hack spec.
 */
public class Code {

    /** Returns the 3-bit binary code of the dest mnemonic (e.g. "DM" -> "011"). */
    public static String dest(String mnemonic) {
        if (mnemonic == null) mnemonic = "";
        String d1 = mnemonic.contains("A") ? "1" : "0";
        String d2 = mnemonic.contains("D") ? "1" : "0";
        String d3 = mnemonic.contains("M") ? "1" : "0";
        return d1 + d2 + d3;
    }

    /** Returns the 7-bit binary code of the comp mnemonic (a + 6 c-bits). */
    public static String comp(String mnemonic) {
        switch (mnemonic) {
            case "0":   return "0101010";
            case "1":   return "0111111";
            case "-1":  return "0111010";
            case "D":   return "0001100";
            case "A":   return "0110000";
            case "M":   return "1110000";
            case "!D":  return "0001101";
            case "!A":  return "0110001";
            case "!M":  return "1110001";
            case "-D":  return "0001111";
            case "-A":  return "0110011";
            case "-M":  return "1110011";
            case "D+1": return "0011111";
            case "A+1": return "0110111";
            case "M+1": return "1110111";
            case "D-1": return "0001110";
            case "A-1": return "0110010";
            case "M-1": return "1110010";
            case "D+A": return "0000010";
            case "D+M": return "1000010";
            case "D-A": return "0010011";
            case "D-M": return "1010011";
            case "A-D": return "0000111";
            case "M-D": return "1000111";
            case "D&A": return "0000000";
            case "D&M": return "1000000";
            case "D|A": return "0010101";
            case "D|M": return "1010101";
            default:
                throw new IllegalArgumentException("Invalid comp mnemonic: '" + mnemonic + "'");
        }
    }

    /** Returns the 3-bit binary code of the jump mnemonic (e.g. "JNE" -> "101"). */
    public static String jump(String mnemonic) {
        if (mnemonic == null || mnemonic.isEmpty()) return "000";
        switch (mnemonic) {
            case "JGT": return "001";
            case "JEQ": return "010";
            case "JGE": return "011";
            case "JLT": return "100";
            case "JNE": return "101";
            case "JLE": return "110";
            case "JMP": return "111";
            default:
                throw new IllegalArgumentException("Invalid jump mnemonic: '" + mnemonic + "'");
        }
    }
}