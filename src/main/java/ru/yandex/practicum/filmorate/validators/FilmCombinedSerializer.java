/*package ru.yandex.practicum.filmorate.validators;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import org.springframework.boot.jackson.JsonComponent;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;

@JsonComponent
public class FilmCombinedSerializer {

        public static class UserJsonSerializer extends JsonSerializer {

            @Override
            public void serialize(Film film, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("id", film.getId());
                jsonGenerator.


                jsonGenerator.writeEndObject();
            }


        }

        public static class FilmJsonDeserializer extends JsonDeserializer {

            @Override
            public Film deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException, JsonProcessingException {

                TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
                TextNode favoriteColor = (TextNode) treeNode.get(
                        "favoriteColor");
                return new User(Color.web(favoriteColor.asText()));
            }
        }
    }
*/
