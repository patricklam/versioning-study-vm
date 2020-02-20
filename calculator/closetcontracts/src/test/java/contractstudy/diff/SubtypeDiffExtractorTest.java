package contractstudy.diff;

import contractstudy.DiffExtractor;
import contractstudy.SubtypeDiffExtractor;
import org.junit.Test;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class SubtypeDiffExtractorTest {

//    private DiffExtractor diff = new EvolutionDiffExtractor();
    private DiffExtractor diff = new SubtypeDiffExtractor();


    @Test
    public void testDiff() throws Exception {
//        List<DiffRecord>  res = diff.extract();
//
//        assertNotNull(res);
    }
}
