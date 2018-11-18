/*
 * Copyright (C) 2017 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.vsct.tock.bot.connector.whatsapp.model.webhook

import com.fasterxml.jackson.module.kotlin.readValue
import fr.vsct.tock.bot.connector.whatsapp.model.common.WhatsAppError
import fr.vsct.tock.bot.connector.whatsapp.model.common.WhatsAppTextBody
import fr.vsct.tock.shared.jackson.mapper
import fr.vsct.tock.shared.resource
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 *
 */
class WhatsAppMessagesDeserializationTest {

    @Test
    fun `text message is deserialized`() {
        val json = resource("/model/webhook/texto.json")
        val messages: WhatsAppMessages = mapper.readValue(json)
        assertEquals(
            WhatsAppMessages(
                listOf(
                    WhatsAppTextMessage(
                        WhatsAppTextBody("Hello this is an answer"),
                        "ABGGFlA5FpafAgo6tHcNmNjXmuSf",
                        "16315555555",
                        "1518694235",
                        WhatsAppProfile("Kerry Fisher")
                    )
                )
            ),
            messages
        )
    }

    @Test
    fun `location message is deserialized`() {
        val json = resource("/model/webhook/location.json")
        val messages: WhatsAppMessages = mapper.readValue(json)
        assertEquals(
            WhatsAppMessages(
                listOf(
                    WhatsAppLocationMessage(
                        WhatsAppLocation(
                            38.9806263495,
                            -131.9428612257,
                            "Main Street Beach, Santa Cruz, CA",
                            "Main Street Beach",
                            "https://foursquare.com/v/4d7031d35b5df7744"
                        ),
                        "ABGGFlA5FpafAgo6tHcNmNjXmuSf",
                        "16315555555",
                        "1521497875"
                    )
                )
            ),
            messages
        )
    }

    @Test
    fun `image message is deserialized`() {
        val json = resource("/model/webhook/image.json")
        val messages: WhatsAppMessages = mapper.readValue(json)
        assertEquals(
            WhatsAppMessages(
                listOf(
                    WhatsAppImageMessage(
                        WhatsAppAttachment(
                            "/usr/local/wamedia/shared/b1cf38-8734-4ad3-b4a1-ef0c10d0d683",
                            "b1c68f38-8734-4ad3-b4a1-ef0c10d683",
                            "image/jpeg",
                            "29ed500fa64eb55fc19dc4124acb300e5dcc54a0f822a301ae99944db",
                            "Check out my new phone!"
                        ),
                        "ABGGFlA5FpafAgo6tHcNmNjXmuSf",
                        "16315555555",
                        "1521497954"
                    )
                )
            ),
            messages
        )
    }

    @Test
    fun `document message is deserialized`() {
        val json = resource("/model/webhook/document.json")
        val messages: WhatsAppMessages = mapper.readValue(json)
        assertEquals(
            WhatsAppMessages(
                listOf(
                    WhatsAppDocumentMessage(
                        WhatsAppAttachment(
                            "/usr/local/wamedia/shared/fc233119-733f-49c-bcbd-b2f68f798e33",
                            "fc233119-733f-49c-bcbd-b2f68f798e33",
                            "application/pdf",
                            "3b11fa6ef2bde1dd14726e09d3edaf782120919d06f6484f32d5d5caa4b8e",
                            "80skaraokesonglistartist"
                        ),
                        "ABGGFlA5FpafAgo6tHcNmNjXmuSf",
                        "16315555555",
                        "1522189546"
                    )
                )
            ),
            messages
        )
    }

    @Test
    fun `voice message is deserialized`() {
        val json = resource("/model/webhook/audio.json")
        val messages: WhatsAppMessages = mapper.readValue(json)
        assertEquals(
            WhatsAppMessages(
                listOf(
                    WhatsAppVoiceMessage(
                        WhatsAppAttachment(
                            "/usr/local/wamedia/shared/463e/b7ec/ff4e4d9bb1101879cbd411b2",
                            "463eb7ec-ff4e-4d9b-b110-1879cbd411b2",
                            "audio/ogg; codecs=opus",
                            "fa9e1807d936b7cebe63654ea3a7912b1fa9479220258d823590521ef53b0710"
                        ),
                        "ABGGFlA5FpafAgo6tHcNmNjXmuSf",
                        "16315555555",
                        "1521827831"
                    )
                )
            ),
            messages
        )
    }

    @Test
    fun `system message is deserialized`() {
        val json = resource("/model/webhook/system.json")
        val messages: WhatsAppMessages = mapper.readValue(json)
        assertEquals(
            WhatsAppMessages(
                listOf(
                    WhatsAppSystemMessage(
                        WhatsAppTextBody("‎‎+1 (650) 441-2845 added ‎+1 (631) 555-1026‎"),
                        "gBEGkYiEB1VXAglK1ZEqA1YKPrU",
                        "16315551026",
                        "1518707853",
                        groupId = "16504412845-1518707486"
                    )
                )
            ),
            messages
        )
    }

    @Test
    fun `unknown message is deserialized`() {
        val json = resource("/model/webhook/unknown.json")
        val messages: WhatsAppMessages = mapper.readValue(json)
        assertEquals(
            WhatsAppMessages(
                listOf(
                    WhatsAppUnknownMessage(
                        listOf(
                            WhatsAppError(
                                501,
                                "Unknown message type",
                                "Message type is not currently supported"
                            )
                        ),
                        "ABGGFRBzFymPAgo6N9KKs7HsN6eB",
                        "16315555555",
                        "1531933468"
                    )
                )
            ),
            messages
        )
    }
}