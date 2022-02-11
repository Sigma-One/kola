import exception.MissingFieldException
import exception.NonUniqueNameException
import kotlin.test.Test
import kotlin.test.assertFailsWith

/** ArgumentCreationTest
 * @author  Sigma-One
 * @created 11/02/2022 03:32
 **/
class ArgumentCreationTest {
    @Test
    fun testArgumentCreationLongShortSuccess() {
        parser { argument("test_argument") {
            shorthand = 't'
            longhand = "test"
            help = "test argument"
        }}
    }

    @Test
    fun testArgumentCreationLongOnlySuccess() {
        parser { argument("test_argument") {
            longhand = "test"
            takeValues = 25
        }}
    }

    @Test
    fun testArgumentCreationShortOnlySuccess() {
        parser { argument("test_argument") {
            shorthand = 't'
            help = "test argument"
        }}
    }

    @Test
    fun testArgumentCreationFlagsMissingFail() {
        assertFailsWith<MissingFieldException> {
            parser { argument("test_argument") { /* Empty argument with no flags; bad */ } }
        }
    }

    @Test
    fun testMultipleArgumentCreationSuccess() {
        parser {
            argument("test_argument") {
                shorthand = 't'
                help = "test argument"
            }
            argument("test_argument_2") {
                shorthand = 'a'
                takeValues = -1
            }
        }
    }

    @Test
    fun testMultipleArgumentCreationSameNameFail() {
        assertFailsWith<NonUniqueNameException> {
            parser {
                argument("test_argument") {
                    shorthand = 't'
                    help = "test argument"
                }
                argument("test_argument") {
                    shorthand = 'a'
                    takeValues = 2
                }
            }
        }
    }
}