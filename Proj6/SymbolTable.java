import java.util.HashMap;
import java.util.Map;

/**
 * SymbolTable
 * Keeps a correspondence between symbolic labels/variables and their
 * numeric addresses (RAM or ROM). Initialized with the Hack predefined
 * symbols (R0..R15, SCREEN, KBD, SP, LCL, ARG, THIS, THAT).
 */
public class SymbolTable {

    private final Map<String, Integer> table;

    /** Constructor: creates a new symbol table and adds the predefined symbols. */
    public SymbolTable() {
        table = new HashMap<>();

        table.put("SP", 0);
        table.put("LCL", 1);
        table.put("ARG", 2);
        table.put("THIS", 3);
        table.put("THAT", 4);
        table.put("SCREEN", 16384);
        table.put("KBD", 24576);

        for (int i = 0; i <= 15; i++) {
            table.put("R" + i, i);
        }
    }

    /** Adds the pair (symbol, address) to the table. */
    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }

    /** Does the symbol table contain the given symbol? */
    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    /** Returns the address associated with the symbol. */
    public int getAddress(String symbol) {
        return table.get(symbol);
    }
}