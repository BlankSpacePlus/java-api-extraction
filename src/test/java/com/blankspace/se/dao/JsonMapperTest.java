package com.blankspace.se.dao;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.blankspace.se.pojo.JavaMethodCode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapperTest {

    @Test
    public void writeAndReadJsonCode() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String repo = "googleapis/google-cloud-java";
        String path = "google-cloud-clients/google-cloud-pubsub/src/main/java/com/google/cloud/pubsub/v1/SubscriptionAdminClient.java";
        String func_name = "SubscriptionAdminClient.modifyPushConfig";
        String original_string = "public final void modifyPushConfig(String subscription, PushConfig pushConfig) {\n\n    ModifyPushConfigRequest request =\n        ModifyPushConfigRequest.newBuilder()\n            .setSubscription(subscription)\n            .setPushConfig(pushConfig)\n            .build();\n    modifyPushConfig(request);\n  }";
        String language = "java";
        String code = "public final void modifyPushConfig(String subscription, PushConfig pushConfig) {\n\n    ModifyPushConfigRequest request =\n        ModifyPushConfigRequest.newBuilder()\n            .setSubscription(subscription)\n            .setPushConfig(pushConfig)\n            .build();\n    modifyPushConfig(request);\n  }";
        String[] code_tokens_array = {"public", "final", "void", "modifyPushConfig", "(", "String", "subscription", ",", "PushConfig", "pushConfig", ")", "{", "ModifyPushConfigRequest", "request", "=", "ModifyPushConfigRequest", ".", "newBuilder", "(", ")", ".", "setSubscription", "(", "subscription", ")", ".", "setPushConfig", "(", "pushConfig", ")", ".", "build", "(", ")", ";", "modifyPushConfig", "(", "request", ")", ";", "}"};
        List<String> code_tokens = Arrays.asList(code_tokens_array);
        String docstring = "Modifies the `PushConfig` for a specified subscription.\n\n<p>This may be used to change a push subscription to a pull one (signified by an empty\n`PushConfig`) or vice versa, or change the endpoint URL and other attributes of a push\nsubscription. Messages will accumulate for delivery continuously through the call regardless of\nchanges to the `PushConfig`.\n\n<p>Sample code:\n\n<pre><code>\ntry (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {\nProjectSubscriptionName subscription = ProjectSubscriptionName.of(\"[PROJECT]\", \"[SUBSCRIPTION]\");\nPushConfig pushConfig = PushConfig.newBuilder().build();\nsubscriptionAdminClient.modifyPushConfig(subscription.toString(), pushConfig);\n}\n</code></pre>\n\n@param subscription The name of the subscription. Format is\n`projects/{project}/subscriptions/{sub}`.\n@param pushConfig The push configuration for future deliveries.\n<p>An empty `pushConfig` indicates that the Pub/Sub system should stop pushing messages\nfrom the given subscription and allow messages to be pulled and acknowledged - effectively\npausing the subscription if `Pull` or `StreamingPull` is not called.\n@throws com.google.api.gax.rpc.ApiException if the remote call fails";
        String[] docstring_tokens_array = {"Modifies", "the", "PushConfig", "for", "a", "specified", "subscription", "."};
        List<String> docstring_tokens = Arrays.asList(docstring_tokens_array);
        String sha = "d2f0bc64a53049040fe9c9d338b12fab3dd1ad6a";
        String url = "https://github.com/googleapis/google-cloud-java/blob/d2f0bc64a53049040fe9c9d338b12fab3dd1ad6a/google-cloud-clients/google-cloud-pubsub/src/main/java/com/google/cloud/pubsub/v1/SubscriptionAdminClient.java#L1291-L1299";
        String partition = "train";
        JavaMethodCode javaMethodCode = new JavaMethodCode(repo, path, func_name, original_string, language, code, code_tokens, docstring, docstring_tokens, sha, url, partition);
        // 写为字符串
        String text = mapper.writeValueAsString(javaMethodCode);
        // 写入文件
        mapper.writeValue(new File("code.json"), javaMethodCode);
        // 写入字节流
        // byte[] bytes = mapper.writeValueAsBytes(javaMethodCode);
        // System.out.println(text);
        // 从字符串中读取
        // JavaMethodCode newJavaMethodCode = mapper.readValue(text, JavaMethodCode.class);
        // System.out.println(newJavaMethodCode);
        // 从字节流中读取
        // JavaMethodCode newJavaMethodCode = mapper.readValue(bytes, JavaMethodCode.class);
        // System.out.println(newJavaMethodCode);
        // 从文件中读取
        JavaMethodCode newJavaMethodCode = mapper.readValue(new File("code.json"), JavaMethodCode.class);
        System.out.println(newJavaMethodCode);
    }

}
