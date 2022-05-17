
<h1 align="center">VKListeningBot - Накрутка прослушиваний ВКонтакте</h1>

<h1 align="center">-Зависимости- </h1>

```xml
<dependency>
    <groupId>commons-cli</groupId>
    <artifactId>commons-cli</artifactId>
    <version>1.5.0</version>
</dependency>

<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>3.141.59</version>
</dependency>

<dependency>
    <groupId>net.lingala.zip4j</groupId>
    <artifactId>zip4j</artifactId>
    <version>2.6.4</version>
</dependency>
```
<h1 align="center">-Использование-</h1>

```sh
java -jar VKListeningBot.jar -l <login> -p <password> -link <album/playlist url>
```

<h2 align="center">Скрыть консоль</h1>

```sh
javaw -jar VKListeningBot.jar -l <login> -p <password> -link <album/playlist url>
```

<h1 align="center">-Накрутка через консоль браузера-</h1>

```js
var getted = false;

const sleep = (milliseconds) => {
  return new Promise(resolve => setTimeout(resolve, milliseconds))
}

async function listeningBot() {
    audios = document.getElementsByClassName('blind_label _audio_row__play_btn')
    durations = document.getElementsByClassName('audio_row__duration audio_row__duration-s _audio_row__duration')
    for (let i = 0; i < 9999999999; i++) {
        k = Math.round(Math.random() * audios.length)
        if (k != 0) {
            k -= 1
        }
        audios[k].click()
        duration = durations[k].textContent
        if (audios.length == 1 && !getted) {
            listeningTime = duration.split(':')[0] * 60 + duration.split(':')[1] * 1 + 8
            getted = true;
        } else {
                    listeningTime = (duration.split(':')[0] * 60 + duration.split(':')[1] * 1) / 2
        }
        await sleep(listeningTime * 1000)
    }
}

listeningBot();
```