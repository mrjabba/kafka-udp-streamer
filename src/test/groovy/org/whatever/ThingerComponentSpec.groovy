package org.whatever

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ThingerComponentSpec extends Specification {

    @Autowired
    Environment environment

    def setup() {
    }

    def "verify we can load spring stuff"() {
        when:
        String[] profiles = environment.defaultProfiles

        then:
        assert profiles.length > 0
    }
}