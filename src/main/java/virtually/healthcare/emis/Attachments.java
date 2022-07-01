package virtually.healthcare.emis;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Attachments {

    @Autowired
    ApplicationContext context;

    FhirContext ctx;

    public static void main(String[] args) {
        System.setProperty("hawtio.authenticationEnabled", "false");
        System.setProperty("management.security.enabled","false");
        System.setProperty("management.contextPath","");
        SpringApplication.run(Attachments.class, args);

    }

    @Bean
    public ServletRegistrationBean servletRegistrationEMISBean(FhirContext ctx) {
        ServletRegistrationBean registration = new ServletRegistrationBean(new RestfulServer(context,ctx), "/R4/*");
        registration.setName("FhirServlet");
        registration.setLoadOnStartup(1);
        return registration;
    }

    @Bean
    public FhirContext fhirContext() {
        if (this.ctx == null) {
            this.ctx = FhirContext.forR4();
        }
        this.ctx.setParserErrorHandler(new StrictErrorHandler());
        return this.ctx;
    }
}
