import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

import java.io.IOException;

public class BatchTimeLoggingInterceptorTest extends LoggingInterceptor{
    private long batchTime;

    public BatchTimeLoggingInterceptorTest(boolean theVerbose) {
        super(theVerbose);
        this.batchTime = 0;
    }

    @Override
    public void interceptResponse(IHttpResponse theResponse) throws IOException {
        batchTime += 500;
    }

    public long getBatchTime() {
        return batchTime;
    }

    public void setBatchTime(long batchTime) {
        this.batchTime = batchTime;
    }
}
