import exception.MissingFieldException
import exception.NonUniqueNameException
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * ArgumentParsingTest
 *
 * @author Sigma-One
 * @created 11/02/2022 04:34
 */
class ArgumentParsingTest {
    @Test
    fun parseArgumentsTestSuccess() {
        parser {
            argument("help") {
                shorthand = 'h'
                longhand = "help"
            }
            argument("query") {
                shorthand = 'q'
                longhand = "query"
                takeValues = 1
            }
            argument("verbose") {
                shorthand = 'v'
            }
            argument("install") {
                shorthand = 'i'
                takeValues = -1
            }
        }.parse(
            "-h",
            "-vq",
            "spam",
            "--help",
            "-i", "eggs",
            "-i", "toast",
            "-vvv",
            "-i", "trans" ,"rights",
            "-v"
        )
    }

    @Test
    fun parseArgumentsTestExtraValuesFail() {
        assertFailsWith<IllegalArgumentException> {
            parser {
                argument("query") {
                    shorthand = 'q'
                    longhand = "query"
                    takeValues = 1
                }
            }.parse(
                "-q",
                "spam",
                "eggs"
            )
        }
    }

    @Test
    fun parseArgumentsTestExtraFirstValuesFail() {
        assertFailsWith<IllegalArgumentException> {
            parser {
                argument("query") {
                    shorthand = 'q'
                    longhand = "query"
                    takeValues = 1
                }
            }.parse(
                "foobar",
                "-q",
                "spam",
                "eggs"
            )
        }
    }
}