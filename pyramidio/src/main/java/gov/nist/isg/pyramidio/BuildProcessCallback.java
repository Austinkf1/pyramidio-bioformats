package gov.nist.isg.pyramidio;
import java.io.IOException;

public interface BuildProcessCallback {
    void init(long totalArea);

    void update(long processedArea);
}
