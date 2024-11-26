package kataster.cw1_kat;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

public class KonturTester {
    @ParameterizedTest
    @CsvSource({
            "LsV, ''",
            "RIIIa, ''",
            "PsIVa, ''", // fail bo dla ps klasy bez ab
            "Lzr, ''", // nie jestem pewien ale chyba glownie problem ze on musi byc w parze
            "E, ''",
            " IV, ''",
            "Wsr-RVIz, ''"

    })
    public void testTheStatusOfContourForSingleConstructorParameter(String input, String expected) {
        Kontur kontur = new Kontur(input);
        assertEquals(expected, kontur.getStatus());
    }
    @ParameterizedTest
    @CsvSource({
            "E, Wm, ''",
            "N, RVI, ''",
            "Wsr, RVIz, ''"
    })
    public void testTheStatusOfContourForTwoConstructorParameters(String first, String second, String expected) {
        Kontur kontur = new Kontur(first, second);
        assertEquals(expected, kontur.getStatus());
    }
}