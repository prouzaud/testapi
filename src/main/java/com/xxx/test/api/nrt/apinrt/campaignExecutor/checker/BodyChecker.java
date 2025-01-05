package com.xxx.test.api.nrt.apinrt.campaignExecutor.checker;

import com.xxx.test.api.nrt.apinrt.campaignExecutor.exceptions.ExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xxx.test.api.nrt.apinrt.campaignExecutor.checker.TokenType.REGEX;
import static com.xxx.test.api.nrt.apinrt.campaignExecutor.checker.TokenType.TEXT;

@Component
public class BodyChecker {

    @Value("${apiNrt.inputs.regexBegin}")
    private String regexBeginSymbol;
    @Value("${apiNrt.inputs.regexEnd}")
    private String regexEndSymbol;


    public boolean matches(String expectedBody, String actualBody) {
        var pattern = compileExpression(expectedBody);
        Matcher matcher = pattern.matcher(actualBody);
        return matcher.matches();
    }

    private Pattern compileExpression(String expression) {
        var tokens = splitExpression(expression);
        StringBuilder result = new StringBuilder();
        for (var token : tokens) {
            if (token.type() == TEXT) {
                String textPattern = Pattern.quote(token.value());
                result.append(textPattern);
            } else if (token.type() == REGEX) {
                result.append(token.value());
            }
        }
        return Pattern.compile(result.toString());
    }

    private List<Token> splitExpression(String expression) {

        List<Token> tokens = new ArrayList<>();
        int regexIndex = expression.indexOf(regexBeginSymbol);

        if (regexIndex == -1) {
            tokens.add(new Token(TEXT, expression));
        } else if (regexIndex > 0) {
            String lastPart = parseTextPrefix(expression, regexIndex, tokens);
            tokens.addAll(splitExpression(lastPart));
        } else {
            String toBeParsed = parseRegex(expression, tokens);
            tokens.addAll(splitExpression(toBeParsed));
        }
        return tokens;
    }

    private static String parseTextPrefix(String expression, int regexIndex, List<Token> tokens) {
        String prefix = expression.substring(0, regexIndex);
        tokens.add(new Token(TEXT, prefix));
        return expression.substring(regexIndex);
    }

    private String parseRegex(String expression, List<Token> tokens) {
        String expr = skip(expression, regexBeginSymbol);
        int endIndex = expr.indexOf(regexEndSymbol);
        if (endIndex == -1) {
            throw new ExecutionException("The expression " + expression + " declare a regex (started by "+ regexBeginSymbol + "that is not closed by a " + regexEndSymbol +".");
        }
        String regex = expr.substring(0, endIndex);
        tokens.add(new Token(REGEX, regex));
        String toBeParsed = expr.substring(endIndex + regexEndSymbol.length());
        return toBeParsed;
    }

    private String skip(String expression, String symbol) {
        return expression.substring(symbol.length());
    }


    public static void main(String... args) {
        Pattern monPattern = buildPattern("{\"timestamp\":\"",
                "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}[+\\-]\\d{2}:\\d{2}",
                "\",\"status\":404,\"error\":\"Not Found\",\"path\":\"/test/unexpected\"}");

        System.out.println("Le pattern compilé est : " + monPattern.pattern());
        // => \QABC-\E[0-9]+\Q-XYZ\E

        // On peut maintenant tester un exemple
        String exemple = "{\"timestamp\":\"2025-01-05T19:16:14.411+00:00\",\"status\":404,\"error\":\"Not Found\",\"path\":\"/test/unexpected\"}";
        boolean ok = monPattern.matcher(exemple).matches();
        System.out.println("Correspondance ? " + ok); // true si la chaîne matche
    }

    public static Pattern buildPattern(String prefixFixe, String partieRegex, String suffixFixe) {
        // On quote préfixe et suffixe, mais on ne touche pas à la partie regex
        String patternString = Pattern.quote(prefixFixe)
                + partieRegex
                + Pattern.quote(suffixFixe);

        return Pattern.compile(patternString);
    }
}