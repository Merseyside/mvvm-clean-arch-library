
package com.upstream.basemvvmimpl.data.cache.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Json Serializer/Deserializer.
 */
@Singleton
public class Serializer {

  private final Gson gson;

  @Inject
  Serializer() {
      gson = new GsonBuilder()
          .setLenient()
          .create();
  }

  public String serialize(Object object, Class clazz) {
    return gson.toJson(object, clazz);
  }

  public <T> T deserialize(String string, Class<T> clazz) {
    return gson.fromJson(string, clazz);
  }
}
