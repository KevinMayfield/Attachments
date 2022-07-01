package virtually.healthcare.emis;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.hl7.fhir.r4.model.DocumentReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentReferenceProvider implements IResourceProvider  {

    @Autowired
    FhirContext ctx;

    @Override
    public Class<DocumentReference> getResourceType() {
        return DocumentReference.class;
    }

    @Search
    public List<DocumentReference> search(HttpServletRequest httpRequest
    ) throws Exception {
        try {
            List<DocumentReference> documentReferences = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("C:\\patientattachments\\"
            ))) {
                for (Path path : stream) {
                    if (!Files.isDirectory(path)) {
                        DocumentReference documentReference = new DocumentReference();
                        documentReference.setId(path.getFileName().toString());
                        documentReferences.add(documentReference);
                    }
                }
            }
            return documentReferences;
        } catch (Exception ex) {
            throw ex;
        }
    }

}
