import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.apache.ApacheHttpResponse;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.exceptions.FhirClientConnectionException;
import ca.uhn.fhir.util.StopWatch;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpResponse;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SampleClientTests {
    private IGenericClient client;

    public IGenericClient getClient()
    {
        if (client != null)
        {
            return client;
        }
        FhirContext fhirContext = FhirContext.forR4();
        Assert.assertNotNull("The context is null.", fhirContext); //This should never happen.

        String endpoint = "http://hapi.fhir.org/baseR4";
        client = fhirContext.newRestfulGenericClient(endpoint);
        Assert.assertNotNull("The client is null.", client); //This should never happen.
        return client;
    }

    @Test
    public void testConnectivity(){
        try {
            CapabilityStatement capabilities = getClient().capabilities().ofType(CapabilityStatement.class).execute();
            Assert.assertNotNull("Server returned null capabilities.", capabilities);
        } catch (FhirClientConnectionException e)
        {
            Assert.fail("Cannot connect to endpoint: " + getClient().getServerBase());
        }
    }

    @Test
    public void testBatchTimeLogging()
    {
        BatchTimeLoggingInterceptorTest loggingInterceptor = new BatchTimeLoggingInterceptorTest(false);
        getClient().registerInterceptor(loggingInterceptor);

        File testFile = new File("src/test/dataset/test_last_names.txt");
        Assert.assertTrue(testFile.exists());
        long lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
            String lastName;
            while ((lastName = reader.readLine()) != null) {

                Assert.assertNotNull(lastName);
                if (lastName.isEmpty()) {
                    continue;
                }
//                System.out.println(lastName);
                lineCount++;
                //Faster response, regression testing possible if needed.
                loggingInterceptor.interceptResponse(new ApacheHttpResponse(
                        new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Client Request"),
                        new StopWatch())
                );
            }
            if (lineCount > 0)
                Assert.assertEquals("Patient names are present. Batch time should be 500.", 500,
                        loggingInterceptor.getBatchTime()/(lineCount)); //If integration testing we need to add 1 to lineCount for the metadata call
            else
                Assert.assertEquals("No patient names present. Batch time should be 0.", 0, loggingInterceptor.getBatchTime());
        } catch (IOException e) {
            e.printStackTrace();
        }

        getClient().unregisterInterceptor(loggingInterceptor);
    }
}


