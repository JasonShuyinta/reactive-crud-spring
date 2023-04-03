# REACTIVE CRUD SPRING WITH MONGODB

In this project we explore the possibilities of Reactive programming with Spring connecting to a MongoDB database.

The project uses the following tech stack:

- Java 17
- Spring 3.0.5
- Webflux
- Reactive MongoDB
- MapStruct
- Lombok

First, download the necessary dependencies as follows:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.3.Final</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.5.3.Final</version>
    </dependency>
</dependencies>
```
## Controller

At the controller level in order to get a stream of data we need to use the Flux<Object> for a list, while we use Mono<Object> for the
normal object.

As you can see from the example below, we pass the output object to the Flux class, in our case is the MessageOutput class.

```java
package com.dotjson;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public Flux<MessageOutput> getMessages() {
        log.info("START getMessages");
        return messageService.getMessages();
    }

    @GetMapping("/{id}")
    public Mono<MessageOutput> getMessage(@PathVariable String id) {
        log.info("START getMessage with id {} ", id);
        return messageService.getMessage(id);
    }
}
```


## Service 
At the service layer we need to call the repository and the method needed and after that map the result to the business logic
needed in the specific case. In the below example you can see that all we needed was to map each MessageEntity to a MessageOutput object, as
that is the object that will be returned to the client.

```java
package com.dotjson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageMapper messageMapper;

    public Flux<MessageOutput> getMessages() {
        return messageRepository.findAll().map(messageMapper::entityToOutput);
    }

    public Mono<MessageOutput> getMessage(String id) {
        return messageRepository.findById(id).map(messageMapper::entityToOutput);
    }

    public Mono<MessageOutput> saveMessage(Mono<MessageInput> messageInputMono) {
        return messageInputMono.map(messageMapper::inputToEntity)
                .map(m -> {
                    m.setTime(LocalDateTime.now());
                    return m;
                })
                .flatMap(messageRepository::insert)
                .map(messageMapper::entityToOutput);
    }

    public Mono<MessageOutput> updateMessage(Mono<MessageInput> messageInputMono, String id) {
        return messageRepository.findById(id)
                .flatMap(m -> messageInputMono.map(messageMapper::inputToEntity)
                        .doOnNext(e->e.setId(id)))
                .flatMap(messageRepository::save)
                .map(messageMapper::entityToOutput);
    }

    public Mono<Void> deleteMessage(String id) {
        return messageRepository.deleteById(id);
    }
}
```
If some business logic is needed (like in the **saveMessage** or the **updateMessage** methods), they all need to be done in a functional
programming style. 
Here follows the difference between **map** and **flatMap**:
```text
Both map and flatMap can be applied to a Stream<T> and they both return a Stream<R>. 
The difference is that the map operation produces one output value for each input value, whereas the flatMap operation 
produces an arbitrary number (zero or more) values for each input value.
```