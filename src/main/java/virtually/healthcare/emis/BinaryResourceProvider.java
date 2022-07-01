package virtually.healthcare.emis;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.juli.FileHandler.DEFAULT_BUFFER_SIZE;


@Component
public class BinaryResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;


    private static final Logger log = LoggerFactory.getLogger(BinaryResourceProvider.class);

    @Override
    public Class<Binary> getResourceType() {
        return Binary.class;
    }

    @Operation(name = "$dds", idempotent = false, manualRequest = true)
    public Binary getDDS(HttpServletRequest httpRequest, @OperationParam(name="id") String fileName
    ) throws IOException {

            MethodOutcome method = new MethodOutcome();
            method.setCreated(true);
            OperationOutcome opOutcome = new OperationOutcome();

            method.setOperationOutcome(opOutcome);

            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString());
            File file = new File("c:/patientattachments/"+ fileName);
            try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
                int read;
                byte[] bytes = new byte[500];
                while ((read = httpRequest.getInputStream().read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
            Binary binary = new Binary();
            binary.setId(fileName);
            return binary;

    }

    @Delete
    public MethodOutcome deleteBinary(HttpServletRequest httpRequest,
                                           @IdParam IdType idType) throws Exception {

        //   AuditEvent auditEvent = awsAuditEvent.createAudit(httpRequest, getResourceType().getSimpleName(), patientId.getValue(), null);

        MethodOutcome method = new MethodOutcome();
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);

        if (idType != null) {
            System.out.println(idType.getIdPart());
            String fileName = idType.getIdPart();
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString());
            Path fileToDeletePath = Paths.get("c:/patientattachments/"+fileName);
            Files.delete(fileToDeletePath);
        }

        return method;
    }


}
