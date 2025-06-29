# Домашнее задание: Автоматическое логирование.

## Цель

Понять как реализуется AOP, какие для этого есть технические средства.

## Описание / Пошаговая инструкция выполнения домашнего задания

Разработайте такой функционал:  
метод класса можно пометить самодельной аннотацией @Log, например, так:

```java
class TestLogging implements TestLoggingInterface {
    @Log
    public void calculation(int param) {}
}
```

При вызове этого метода "автомагически" в консоль должны логироваться значения параметров.  
Например так:

```java
class Demo {
    public void action() {
        new TestLogging().calculation(6);
    }
}
```

В консоле дожно быть:

```
executed method: calculation, param: 6
```

Обратите внимание: явного вызова логирования быть не должно.

Учтите, что аннотацию можно поставить, например, на такие методы:

- `public void calculation(int param1)`
- `public void calculation(int param1, int param2)`
- `public void calculation(int param1, int param2, String param3)`

P.S.  
Выбирайте реализацию с ASM, если действительно этого хотите и уверены в своих силах.

---

## Сборка и запуск

### Вариант 1 — собрать fat JAR только для модуля:
Перед запуском любой из команд обязательно перейдите в корень проекта, где находится файл gradlew
```bash
./gradlew :hw05-byteCodes:shadowJar
```

### Вариант 2 — собрать всё через корневой `build.gradle.kts`:

```bash
./gradlew build
```
