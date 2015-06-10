package gov.cbp.taspd.gtas.config;

import gov.cbp.taspd.gtas.rule.RuleService;
import gov.cbp.taspd.gtas.rule.RuleServiceImpl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
/**
 * The configuration class can be imported into an XML configuration by:<br>
 * <context:annotation-config/>
 * <bean class="gov.cbp.taspd.gtas.config.RuleServiceConfig"/>
 * 
 * @author GTAS3
 *
 */
//@ImportResource("another-application-context.xml")
//@Import(OtherConfiguration.class)
@Configuration
@ComponentScan("gov.cbp.taspd.gtas")
@PropertySource("classpath:rulesvc.properties")
public class RuleServiceConfig {
    @Resource
    private Environment env;

    @Autowired
    @Value("${some.interesting.property}")
    private String someInterestingProperty;
    
    @Bean(name = "ruleEngine")
	  public RuleService getRuleService() {
	    return new RuleServiceImpl();
	  }


}
