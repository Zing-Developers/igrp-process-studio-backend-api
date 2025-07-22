/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.shared.application.constants;

import cv.igrp.framework.core.domain.IgrpEnum;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ProcessDefinitionState implements IgrpEnum<String> {

  DRAFT("DRAFT", "Rascunho. Ainda não está pronto para uso. Pode ser editado livremente."),
    VALIDATED("VALIDATED", "Validado internamente, mas ainda não publicado. Passou por revisão."),
    PUBLISHED("PUBLISHED", "Disponível para instâncias. Pode ser usado para iniciar processos."),
    DEPRECATED("DEPRECATED", "Está em uso, mas desaconselhado para novos processos."),
    ARCHIVED("ARCHIVED", "Não pode mais ser instanciado. Guardado apenas para histórico."),
    DISABLED("DISABLED", "Temporariamente desativado. Nenhuma instância pode ser criada."),
    ERROR("ERROR", "Erro na implantação/parsing")
  ;

  private final String code;
  private final String description;

  ProcessDefinitionState(String code, String description) {
    this.code = code;
    this.description = description;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getDescription() {
    return description;
  }

  /**
  * Pre-built maps for fast lookup.
  */
  private static final Map<String, ProcessDefinitionState> CODE_MAP = Arrays.stream(values())
          .collect(Collectors.toMap(ProcessDefinitionState::getCode, Function.identity()));

  /**
  * Attempts to find the enum value associated with the given code.
  * @param code The code to look up
  * @return An Optional containing the enum value if found, empty Optional otherwise
  */
  public static Optional<ProcessDefinitionState> fromCode(String code) {
    return Optional.ofNullable(CODE_MAP.get(code));
  }

  /**
  * Finds the enum value associated with the given code or throws an exception if not found.
  * @param code The code to look up
  * @return The enum value for the given code
  * @throws IllegalArgumentException if no enum value exists for the given code
  */
  public static ProcessDefinitionState fromCodeOrThrow(String code) {
    return fromCode(code).orElseThrow(() -> new IllegalArgumentException("Invalid ProcessDefinitionState for this code: " + code));
  }

  /**
  * Returns a map of code to description.
  */
  public static Map<String, String> codeDescriptionMap() {
    return CODE_MAP.values().stream().collect(Collectors.toMap(ProcessDefinitionState::getCode, ProcessDefinitionState::getDescription));
  }

}
