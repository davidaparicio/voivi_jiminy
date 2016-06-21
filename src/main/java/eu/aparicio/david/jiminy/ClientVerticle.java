package eu.aparicio.david.jiminy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import java.security.SecureRandom;

public class ClientVerticle extends AbstractVerticle {
    private static Logger logger = Logger.getAnonymousLogger();
    EventBus eventBus;

    @Override
    public void start() throws Exception {

        logger.info("[ClientVerticle] Starting in " + Thread.currentThread().getName());

        String[] Speech;
        Speech = new String[20];
        Speech[0] = "Mr. Speaker, Mr. Vice President, Members of Congress, my fellow Americans:";
        Speech[1] = "Tonight marks the eighth year I've come here to report on the State of the Union. And for this final one, I'm going to try to make it shorter. I know some of you are antsy to get back to Iowa.";
        Speech[2] = "I also understand that because it's an election season, expectations for what we'll achieve this year are low. Still, Mr. Speaker, I appreciate the constructive approach you and the other leaders took at the end of last year to pass a budget and make tax cuts permanent for working families. So I hope we can work together this year on bipartisan priorities like criminal justice reform, and helping people who are battling prescription drug abuse. We just might surprise the cynics again.";
        Speech[3] = "But tonight, I want to go easy on the traditional list of proposals for the year ahead. Don't worry, I've got plenty, from helping students learn to write computer code to personalizing medical treatments for patients. And I'll keep pushing for progress on the work that still needs doing. Fixing a broken immigration system. Protecting our kids from gun violence. Equal pay for equal work, paid leave, raising the minimum wage. All these things still matter to hardworking families; they are still the right thing to do; and I will not let up until they get done.";
        Speech[4] = "But for my final address to this chamber, I don't want to talk just about the next year. I want to focus on the next five years, ten years, and beyond.";
        Speech[5] = "I want to focus on our future.";
        Speech[6] = "We live in a time of extraordinary change – change that's reshaping the way we live, the way we work, our planet and our place in the world. It's change that promises amazing medical breakthroughs, but also economic disruptions that strain working families. It promises education for girls in the most remote villages, but also connects terrorists plotting an ocean away. It's change that can broaden opportunity, or widen inequality. And whether we like it or not, the pace of this change will only accelerate.";
        Speech[7] = "America has been through big changes before – wars and depression, the influx of immigrants, workers fighting for a fair deal, and movements to expand civil rights. Each time, there have been those who told us to fear the future; who claimed we could slam the brakes on change, promising to restore past glory if we just got some group or idea that was threatening America under control. And each time, we overcame those fears. We did not, in the words of Lincoln, adhere to the dogmas of the quiet past. Instead we thought anew, and acted anew. We made change work for us, always extending America's promise outward, to the next frontier, to more and more people. And because we did – because we saw opportunity where others saw only peril – we emerged stronger and better than before.";
        Speech[8] = "What was true then can be true now. Our unique strengths as a nation – our optimism and work ethic, our spirit of discovery and innovation, our diversity and commitment to the rule of law – these things give us everything we need to ensure prosperity and security for generations to come.";
        Speech[9] = "In fact, it's that spirit that made the progress of these past seven years possible. It's how we recovered from the worst economic crisis in generations. It's how we reformed our health care system, and reinvented our energy sector; how we delivered more care and benefits to our troops and veterans, and how we secured the freedom in every state to marry the person we love.";
        Speech[10] = "But such progress is not inevitable. It is the result of choices we make together. And we face such choices right now. Will we respond to the changes of our time with fear, turning inward as a nation, and turning against each other as a people? Or will we face the future with confidence in who we are, what we stand for, and the incredible things we can do together?";
        Speech[11] = "So let's talk about the future, and four big questions that we as a country have to answer – regardless of who the next President is, or who controls the next Congress.";
        Speech[12] = "First, how do we give everyone a fair shot at opportunity and security in this new economy?";
        Speech[13] = "Second, how do we make technology work for us, and not against us – especially when it comes to solving urgent challenges like climate change?";
        Speech[14] = "Third, how do we keep America safe and lead the world without becoming its policeman?";
        Speech[15] = "And finally, how can we make our politics reflect what's best in us, and not what's worst?";
        Speech[16] = "Let me start with the economy, and a basic fact: the United States of America, right now, has the strongest, most durable economy in the world. We're in the middle of the longest streak of private-sector job creation in history. More than 14 million new jobs; the strongest two years of job growth since the '90s; an unemployment rate cut in half. Our auto industry just had its best year ever. Manufacturing has created nearly 900,000 new jobs in the past six years. And we've done all this while cutting our deficits by almost three-quarters.";
        Speech[17] = "Anyone claiming that America's economy is in decline is peddling fiction. What is true – and the reason that a lot of Americans feel anxious – is that the economy has been changing in profound ways, changes that started long before the Great Recession hit and haven't let up. Today, technology doesn't just replace jobs on the assembly line, but any job where work can be automated. Companies in a global economy can locate anywhere, and face tougher competition. As a result, workers have less leverage for a raise. Companies have less loyalty to their communities. And more and more wealth and income is concentrated at the very top.";
        Speech[18] = "All these trends have squeezed workers, even when they have jobs; even when the economy is growing. It's made it harder for a hardworking family to pull itself out of poverty, harder for young people to start on their careers, and tougher for workers to retire when they want to. And although none of these trends are unique to America, they do offend our uniquely American belief that everybody who works hard should get a fair shot.";
        Speech[19] = "For the past seven years, our goal has been a growing economy that works better for everybody. We've made progress. But we need to make more. And despite all the political arguments we've had these past few years, there are some areas where Americans broadly agree.";
        final Integer[] id = {0};

        //byte[] sharedKey = new byte[32]; new SecureRandom().nextBytes(sharedKey);
        // The shared key
        byte[] key128 = { (byte)177, (byte)119, (byte) 33, (byte) 13, (byte)164, (byte) 30, (byte)108, (byte)121, (byte)207, (byte)136, (byte)107, (byte)242, (byte) 12, (byte)224, (byte) 19, (byte)226 };
        // Create the header
        JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128GCM);

        logger.info("[ClientVerticle] Started in " + Thread.currentThread().getName());

        vertx.setPeriodic(1000,
                l -> {
                    /*if(id[0] >= 20){
                        id[0] = 0;
                    }*/
                    id[0] = 0;

                    Date today = Calendar.getInstance().getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
                    String timestamp = formatter.format(today);

                    // Set the plain text
                    Payload payload = new Payload(Speech[id[0]++]);
                    // Create the JWE object and encrypt it
                    JWEObject jweObject = new JWEObject(header, payload);
                    try {
                        jweObject.encrypt(new DirectEncrypter(key128));
                    } catch (JOSEException e) {
                        e.printStackTrace();
                    }
                    // Serialise to compact JOSE form...
                    String jweString = jweObject.serialize();

                    vertx.eventBus().send("events",
                            new JsonObject()
                                    .put("message", jweString)
                                    .put("from", "ClientVerticle")
                            , reply -> {
                                if (reply.succeeded()) {
                                    Date today2 = Calendar.getInstance().getTime();
                                    long diff = today2.getTime() - today.getTime();
                                    System.out.println(diff);
                                    //logger.info("[ClientVerticle] Received:\n" + reply.result().body() + " \n[" + Thread.currentThread().getName() + "]\nDone in: " + diff + " ms\n---------------------------------------------------------------------------------------");
                                } else {
                                    logger.info("[ClientVerticle] ERROR:" + reply.cause() + " [" + Thread.currentThread().getName() + "]");
                                }
                            }
                    );
                }
        );
    };
}
