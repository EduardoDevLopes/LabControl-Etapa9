package br.com.labcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Classe de entrada da aplicação LabControl Web.
 *
 * Estende SpringBootServletInitializer para permitir o empacotamento
 * como WAR e deploy em um Tomcat externo (padrão usado no NetBeans),
 * mantendo ao mesmo tempo a possibilidade de rodar localmente via
 * `mvn spring-boot:run` ou executando o WAR diretamente com
 * `java -jar`, graças ao servidor Tomcat embarcado em escopo
 * `provided` no pom.xml.
 */
@SpringBootApplication
public class LabControlApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LabControlApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(LabControlApplication.class, args);
    }
}
