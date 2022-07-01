package virtually.healthcare.emis;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.util.VersionUtil;

import org.springframework.context.ApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import virtually.healthcare.emis.support.FHIRServerProperties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

@WebServlet(urlPatterns = {"/*"}, displayName = "FHIR Server")
public class RestfulServer extends ca.uhn.fhir.rest.server.RestfulServer {

    private static final long serialVersionUID = 1L;

    private final ApplicationContext appCtx;




    RestfulServer(ApplicationContext context,
                  FhirContext ctx
    ) {
        this.appCtx = context;
        this.ctx = ctx;

    }

    private final FhirContext ctx;

    @Override
    public void addHeadersToResponse(HttpServletResponse theHttpResponse) {
        theHttpResponse.addHeader("X-Powered-By", "HAPI FHIR " + VersionUtil.getVersion() + " RESTful Server");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initialize() throws ServletException {
        super.initialize();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));


        FhirVersionEnum fhirVersion = FhirVersionEnum.R4;
        setFhirContext(new FhirContext(fhirVersion));

        setDefaultResponseEncoding(FHIRServerProperties.getDefaultEncoding());

        String serverAddress = FHIRServerProperties.getServerAddress();
        if (serverAddress != null && !serverAddress.isEmpty()) {
            setServerAddressStrategy(new HardcodedServerAddressStrategy(serverAddress));
        }

        List<IResourceProvider> resourceProviders = new ArrayList<>();
        resourceProviders.add(appCtx.getBean(BinaryResourceProvider.class));
        resourceProviders.add(appCtx.getBean(DocumentReferenceProvider.class));
        registerProviders(resourceProviders);

        setFhirContext(appCtx.getBean(FhirContext.class));

        List<Object> plainProviders=new ArrayList<Object>();


        registerProviders(plainProviders);

        /*
         * Enable ETag Support (this is already the default)
         */
        setETagSupport(FHIRServerProperties.getEtagSupport());
        // Replace built in conformance provider (CapabilityStatement)
        /*
       IGConformanceProvider confProvider = new IGConformanceProvider(this,
                serverIgPackage,
                ctx);
        setServerConformanceProvider(confProvider);
*/
        setServerName(FHIRServerProperties.getServerName());
        setServerVersion(FHIRServerProperties.getSoftwareVersion());

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("x-fhir-starter");
        config.addAllowedHeader("Origin");
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("Content-Type");

        config.addAllowedOrigin("*");

        config.addExposedHeader("Location");
        config.addExposedHeader("Content-Location");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));


        // Create the interceptor and register it
        CorsInterceptor interceptor = new CorsInterceptor(config);
        getInterceptorService().registerInterceptor(interceptor);


        // Validation


        setDefaultPrettyPrint(true);
        setDefaultResponseEncoding(EncodingEnum.JSON);

    }


}
