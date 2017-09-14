package org.librairy.harvester.datosgobes.service;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class ParsingService {

    /**
     *
     CC Coordinating conjunction
     CD Cardinal number
     DT Determiner
     EX Existential there
     FW Foreign word
     IN Preposition or subordinating conjunction
     JJ Adjective
     JJR Adjective, comparative
     JJS Adjective, superlative
     LS List item marker
     MD Modal
     NN Noun, singular or mass
     NNS Noun, plural
     NNP Proper noun, singular
     NNPS Proper noun, plural
     PDT Predeterminer
     POS Possessive ending
     PRP Personal pronoun
     PRP$ Possessive pronoun
     RB Adverb
     RBR Adverb, comparative
     RBS Adverb, superlative
     RP Particle
     SYM Symbol
     TO to
     UH Interjection
     VB Verb, base form
     VBD Verb, past tense
     VBG Verb, gerund or present participle
     VBN Verb, past participle
     VBP Verb, non­3rd person singular present
     VBZ Verb, 3rd person singular present
     WDT Whdeterminer
     WP Whpronoun
     WP$ Possessive whpronoun
     WRB Whadverb
     */

    private static final Logger LOG = LoggerFactory.getLogger(ParsingService.class);

    //adding extra terms to standard lucene listByExtension
    //adding extra terms to standard lucene listByExtension
    public static final String customStopWordList = "" +
            "alguna" +
            "algunas" +
            "alguno" +
            "algunos" +
            "algún" +
            "ambos" +
            "ampleamos" +
            "ante" +
            "antes" +
            "aquel" +
            "aquellas" +
            "aquellos" +
            "aqui" +
            "arriba" +
            "atras" +
            "bajo" +
            "bastante" +
            "bien" +
            "cada" +
            "cierta" +
            "ciertas" +
            "ciertos" +
            "como" +
            "con" +
            "conseguimos" +
            "conseguir" +
            "consigo" +
            "consigue" +
            "consiguen" +
            "consigues" +
            "cual" +
            "cuando" +
            "dentro" +
            "donde" +
            "dos" +
            "el" +
            "ellas" +
            "ellos" +
            "empleais" +
            "emplean" +
            "emplear" +
            "empleas" +
            "empleo" +
            "en" +
            "encima" +
            "entonces" +
            "entre" +
            "era" +
            "eramos" +
            "eran" +
            "eras" +
            "eres" +
            "es" +
            "esta" +
            "estaba" +
            "estado" +
            "estais" +
            "estamos" +
            "estan" +
            "estoy" +
            "fin" +
            "fue" +
            "fueron" +
            "fui" +
            "fuimos" +
            "gueno" +
            "ha" +
            "hace" +
            "haceis" +
            "hacemos" +
            "hacen" +
            "hacer" +
            "haces" +
            "hago" +
            "incluso" +
            "intenta" +
            "intentais" +
            "intentamos" +
            "intentan" +
            "intentar" +
            "intentas" +
            "intento" +
            "ir" +
            "la" +
            "largo" +
            "las" +
            "lo" +
            "los" +
            "mientras" +
            "mio" +
            "modo" +
            "muchos" +
            "muy" +
            "nos" +
            "nosotros" +
            "otro" +
            "para" +
            "pero" +
            "podeis" +
            "podemos" +
            "poder" +
            "podria" +
            "podriais" +
            "podriamos" +
            "podrian" +
            "podrias" +
            "por" +
            "por qué" +
            "porque" +
            "puede" +
            "pueden" +
            "puedo" +
            "quien" +
            "sabe" +
            "sabeis" +
            "sabemos" +
            "saben" +
            "saber" +
            "sabes" +
            "ser" +
            "si" +
            "siendo" +
            "sin" +
            "sobre" +
            "sois" +
            "solamente" +
            "solo" +
            "somos" +
            "soy" +
            "su" +
            "sus" +
            "también" +
            "teneis" +
            "tenemos" +
            "tener" +
            "tengo" +
            "tiempo" +
            "tiene" +
            "tienen" +
            "todo" +
            "trabaja" +
            "trabajais" +
            "trabajamos" +
            "trabajan" +
            "trabajar" +
            "trabajas" +
            "trabajo" +
            "tras" +
            "tuyo" +
            "ultimo" +
            "un" +
            "una" +
            "unas" +
            "uno" +
            "unos" +
            "usa" +
            "usais" +
            "usamos" +
            "usan" +
            "usar" +
            "usas" +
            "uso" +
            "va" +
            "vais" +
            "valor" +
            "vamos" +
            "van" +
            "vaya" +
            "verdad" +
            "verdadera\tcierto" +
            "verdadero" +
            "vosotras" +
            "vosotros" +
            "voy" +
            "yo";

    public List<String> customStopWord = Arrays.asList(customStopWordList.split(","));
    private final Escaper escaper = Escapers.builder()
            .addEscape('\'',"")
            .addEscape('('," ")
            .addEscape(')'," ")
            .addEscape('['," ")
            .addEscape(']'," ")
            .addEscape('\"',"")
            .build();

    private StanfordCoreNLP pipeline;


    public ParsingService(){
//        Properties props;
//        props = new Properties();
//        //props.put("annotators", "tokenize, cleanxml, ssplit, pos, lemma, stopword"); //"tokenize, ssplit, pos,
//        // lemma, ner, parse, dcoref"
//        //props.put("annotators", "tokenize, ssplit, pos, lemma, stopword, ner"); //"tokenize, ssplit, pos,
//        props.put("annotators", "tokenize, ssplit, pos, lemma, stopword, ner"); //"tokenize, ssplit, pos,
//
//        // Custom sentence split
//        props.setProperty("ssplit.boundaryTokenRegex", "[.]|[!?]+|[。]|[！？]+");
//
//        // Custom tokenize
////        props.setProperty("tokenize.options","untokenizable=allDelete,normalizeOtherBrackets=false,normalizeParentheses=false");
//        props.setProperty("tokenize.options","untokenizable=noneDelete,normalizeOtherBrackets=false,normalizeParentheses=false");
//
//        // Spanish or English Model
//        props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger");
//
//
//        // Custom stopwords
////        props.setProperty("customAnnotatorClass.stopword", "intoxicant.analytics.coreNlp.StopwordAnnotator");
//        props.setProperty("customAnnotatorClass.stopword", StopWordAnnotatorWrapper.class.getCanonicalName());
//        props.setProperty("stopword-list", customStopWordList);
//
//        // Parallel
//        //props.put("threads", "8");
//        pipeline = new StanfordCoreNLP(props);



        pipeline = new StanfordCoreNLP(
                PropertiesUtils.asProperties(
                        "annotators", "tokenize,ssplit,pos", // no lemma in spanish! - parse,lemma
                        "ssplit.isOneSentence", "false",
                        "pos.model", "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger",
                        "pos.maxlen", "200",
                        "parse.model", "edu/stanford/nlp/models/srparser/spanishSR.ser.gz",
                        "tokenize.language", "es"));






    }

    public String tokenize(String text){
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(escaper.escape(text));

        StringBuilder tokens = new StringBuilder();

        // run all Annotators on this text
        Instant start = Instant.now();
        pipeline.annotate(document);


        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);


        for(CoreMap sentence : sentences){
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class).toLowerCase();
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class).toLowerCase();
//                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
//                System.out.println("Word: " + word +" |PoS:" + pos);
//                // this is the NER label of the token
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                System.out.println("NER -> " + ne);
                if (pos.startsWith("n") || pos.startsWith("vm")) tokens.append(word).append(" ");
            }

//            // this is the parse tree of the current sentence
//            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
//
//            // this is the Stanford dependency graph of the current sentence
//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
        }


        Instant end = Instant.now();
        LOG.debug("parsing elapsed time: " + Duration.between(start,end).toMillis() + "msecs");

        return tokens.toString();
    }

    public static void main(String[] args){

        ParsingService service = new ParsingService();

        String text = "\"La Viceconsejería de Política Lingüística del Gobierno Vasco ha impulsado durante los últimos años varias iniciativas para fomentar el conocimiento y uso del euskara en el ámbito de las nuevas tecnologías.De hecho, se han actualizado y puesto a disposición de la ciudadanía en Internet diversas herramientas del euskara, con el objetivo de que puedan ser consultadas y descargadas desde la Red. Entre ellas destacan el Banco Público de Terminología Euskalterm, el Traductor Automático, diccionarios y software en euskara. Asimismo, también se ha colaborado e impulsado la mejora de otras como la Wikipedia en euskera.Continuando con esta línea, la Viceconsejería decidió en 2012 adquirir la Enciclopedia Euskal Herri Enblematikoa de la editorial OSTOA. Hoy en día se han adquirido 24 tomos, que pone a disposición de la ciudadanía a través de Open Data Euskadi. Los contenidos de la enciclopedia se integrarán en Wikipedia paulatinamente.Por un lado, puedes acceder a la enciclopedia tal como se publicó, en formato PDF. Por otro lado, también ponemos a tu disposición los contenidos en formato Word y, aparte, las imágenes.\"";

        String tokens = service.tokenize(text);

        System.out.println(text);
        System.out.println(tokens);

    }
}
