package cv.igrp.platform.process_manager_studio.shared.config;


import cv.igrp.framework.process.management.integration.core.adapter.IProcessDefinitionAdapter;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.adapter.MockProcessDefinitionClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Esta classe de configuração SÓ é usada em ambiente de desenvolvimento/teste.
 * Ela define beans que têm prioridade sobre os beans de produção.
 */
@Configuration
public class TestBeanConfiguration {

  /**
   * Este método cria um "bean" (um objeto gerido pelo Spring)
   * do nosso MockProcessDefinitionClient.
   *
   * A anotação @Primary é a parte mais importante. Ela diz ao Spring:
   * "Se encontrares outro bean do tipo IProcessDefinitionAdapter, ignora-o.
   * Usa ESTE como o principal."
   */
  @Bean
  @Primary
  public IProcessDefinitionAdapter mockProcessDefinitionAdapter() {
    // Adicionamos um print para sabermos que este código foi executado.
    System.out.println("==========================================================");
    System.out.println("=== MODO DE TESTE LOCAL ATIVO: SDK real desativado. ===");
    System.out.println("==========================================================");

    // Retorna a nossa "ficha de teste".
    return new MockProcessDefinitionClient();
  }
}
