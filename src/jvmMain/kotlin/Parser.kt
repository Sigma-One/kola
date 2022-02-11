import exception.MissingFieldException
import exception.NonUniqueNameException

/** Parser
 * @author  Sigma-One
 * @created 10/02/2022 21:46
 **/

/**
 * Creates a new parser
 * @param   init    An initialiser block for the parser
 **/
fun parser(init: Parser.() -> Unit): Parser {
    val parser = Parser()
    parser.init()
    return parser
}

class Parser internal constructor() {
    private val arguments = arrayListOf<Argument>()

    /**
     * Creates an argument inside a parser
     * @param   name    The argument name, using the longhand flag as the name is good practice
     * @param   init    An initialiser for the argument
     **/
    fun argument(name: String, init: Argument.() -> Unit) {
        // Check that name isn't in use, if is, fail
        if (arguments.map { it.name }.contains(name)) { throw NonUniqueNameException("Argument cannot be called '$name' as it is already in use") }

        val argument = Argument(name)
        argument.init()

        // Check for missing flags, if missing, fail
        if (
           argument.shorthand == null &&
           argument.longhand == null
        ) { throw MissingFieldException("Shorthand and longhand flag are both missing. One or both are required") }

        this.arguments.add(argument)
    }

    private fun getArgumentByShorthand(flag: Char): Argument? {
        val index = arguments.map { it.shorthand }.indexOf(flag)
        return if (index > -1) { arguments[index] } else { null }
    }

    private fun getArgumentByLonghand(flag: String): Argument? {
        val index = arguments.map { it.longhand }.indexOf(flag)
        return if (index > -1) { arguments[index] } else { null }
    }

    private fun getArgumentByName(name: String): Argument? {
        val index = arguments.map { it.name }.indexOf(name)
        return if (index > -1) { arguments[index] } else { null }
    }

    /**
     * Parses a provided argument chain
     * Note that these should only contain flag-type arguments, and handling non-flagged ones is left upon the user of this library
     * There should also not be extra values or an error will be raised
     * TODO: Better errors
     * @param   rawArgs     A vararg containing a string of flag arguments, split on spaces
     * @return   An ArrayList containing each matched argument as an ArgumentMatch instance
     */
    fun parse(vararg rawArgs: String): ArrayList<ArgumentMatch> {
        // Split chains of shorthands
        val args = arrayListOf<String>()
        val matches = arrayListOf<ArgumentMatch>()

        for (raw in rawArgs) {
            // If we have one dash but not two it's a shorthand
            if (raw.startsWith("-") && !raw.startsWith("--")) {
                for (c in raw.removePrefix("-")) {
                    args.add("-$c")
                }
            }
            else { args.add(raw) }
        }

        // Run actual processing loop
        var currentArg: ArgumentMatch? = null
        for (strArg in args) {
            val arg = if (strArg.startsWith("--")) { getArgumentByLonghand(strArg.removePrefix("--")) }
            else if (strArg.startsWith("-")) { getArgumentByShorthand(strArg.removePrefix("-").first()) }
            else {
                if (currentArg?.values != null) {
                    if (
                        currentArg.values!!.size < getArgumentByName(currentArg.name)!!.takeValues ||
                        getArgumentByName(currentArg.name)!!.takeValues == -1
                    ) { currentArg.values!!.add(strArg) }
                    else { throw IllegalArgumentException("Unexpected argument: '$strArg'") }
                }
                else { throw IllegalArgumentException("Unexpected argument: '$strArg'") }
                null
            }

            if (arg != null) {
                if (matches.map { it.name }.contains(arg.name)) { matches[matches.map { it.name }.indexOf(arg.name)].count += 1 }
                else {
                    matches.add(ArgumentMatch(
                        arg.name,
                        1,
                        if (arg.takeValues != 0) { arrayListOf() } else { null }
                    ))
                }
                currentArg = matches[matches.map { it.name }.indexOf(arg.name)]
            }
        }
        return matches
    }
}