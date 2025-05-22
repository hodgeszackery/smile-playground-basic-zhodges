import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SampleClient {
    public static void main(String[] theArgs) {
        // Create a FHIR client
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        BatchTimeLoggingInterceptor loggingInterceptor = new BatchTimeLoggingInterceptor(false);
        client.registerInterceptor(loggingInterceptor);
//        CacheControlDirective cacheControl = new CacheControlDirective().setNoCache(true);

        File nameFile = new File("src/main/dataset/last_names.txt");
        long lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(nameFile))) {
            String lastName;
            while ((lastName = reader.readLine()) != null) {
                lineCount++;
                if (lastName.isEmpty())
                    continue;
                // Search for Patient resources
//                System.out.println(lastName);
                Bundle response = client
                        .search()
                        .forResource("Patient")
                        .where(Patient.FAMILY.matches().value(lastName))
//                        .cacheControl(cacheControl)
                        .returnBundle(Bundle.class)
                        .execute();
//                System.out.println(response.getTotal());
            }
            System.out.println("Average Batch Time: " + loggingInterceptor.getBatchTime() / lineCount + "ms");
            //Run 1 1068
            //Run 2 916
            //Run 3 1079
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}