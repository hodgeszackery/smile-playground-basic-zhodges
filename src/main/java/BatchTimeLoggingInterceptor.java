import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

import java.io.IOException;

public class BatchTimeLoggingInterceptor extends LoggingInterceptor{
    private long batchTime;

    public BatchTimeLoggingInterceptor(boolean theVerbose) {
        super(theVerbose);
        this.batchTime = 0;
    }

    @Override
    public void interceptResponse(IHttpResponse theResponse) throws IOException {
        super.interceptResponse(theResponse);
        batchTime += theResponse.getRequestStopWatch().getMillis();
    }

    public long getBatchTime() {
        return batchTime;
    }

    public void setBatchTime(long batchTime) {
        this.batchTime = batchTime;
    }
}
