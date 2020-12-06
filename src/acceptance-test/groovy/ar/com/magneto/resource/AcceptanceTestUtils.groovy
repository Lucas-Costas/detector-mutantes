package ar.com.magneto.resource

import groovy.json.JsonSlurper
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

import java.nio.charset.StandardCharsets

class AcceptanceTestUtils {

    def static POST(MockMvc mockMvc, String path, String jsonBody) {
        String mediaType = MediaType.APPLICATION_JSON
        return mockMvc
                .perform(
                        MockMvcRequestBuilders.post(path)
                                .contentType(mediaType)
                                .content(jsonBody)
                ).andReturn().response
    }

    def static GET(MockMvc mockMvc, String path) {
        String mediaType = MediaType.APPLICATION_JSON
        return mockMvc
                .perform(
                        MockMvcRequestBuilders.get(path)
                                .contentType(mediaType)
                ).andReturn().response
    }

    static getBody(MockHttpServletResponse postMutantResponse) {
        new JsonSlurper().parseText(postMutantResponse.getContentAsString(StandardCharsets.UTF_8))
    }
}
