package ai.preferred.crawler.azure.translate;

import ai.preferred.venom.request.Request;
import ai.preferred.venom.response.Response;
import ai.preferred.venom.response.VResponse;
import ai.preferred.venom.validator.Validator;
import org.json.JSONArray;

import javax.validation.constraints.NotNull;

public class TranslateValidator implements Validator {

  @Override
  public Status isValid(@NotNull Request request, @NotNull Response response) {
    try {
      final String html = new VResponse(response).getHtml();
      new JSONArray(html);
    } catch (Exception e) {
      return Status.INVALID_CONTENT;
    }
    return Status.VALID;
  }

}
