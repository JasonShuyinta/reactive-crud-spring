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

    @PostMapping
    public Mono<MessageOutput> saveMessage(@RequestBody Mono<MessageInput> messageInputMono) {
        log.info("START saveMessage {} ", messageInputMono.toString());
        return messageService.saveMessage(messageInputMono);
    }

    @PutMapping("/update")
    public Mono<MessageOutput> updateMessage(@RequestBody Mono<MessageInput> messageInputMono, @RequestParam String id) {
        log.info("START updateMessage with id {} and body {} ", id, messageInputMono.toString());
        return messageService.updateMessage(messageInputMono, id);
    }

    @DeleteMapping("/delete")
    public Mono<Void> deleteMessage(@RequestParam String id) {
        log.info("START deleteMessage with id {} ", id);
        return messageService.deleteMessage(id);
    }
}
