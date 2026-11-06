package com.kagura.app.data.local

import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.model.Raridade

object SeedData {
    private val accents = longArrayOf(0xFFB86BFF, 0xFFFF8FB1, 0xFF7FD6F5, 0xFF8B5CF6, 0xFFF472B6, 0xFF22D3EE)

    private fun initialStatus(id: Int) = when {
        id % 7 == 0 -> ItemStatus.COMPLETED
        id % 5 == 0 -> ItemStatus.IN_PROGRESS
        id % 3 == 0 -> ItemStatus.WANT
        else -> ItemStatus.NONE
    }

    private fun parse(type: MediaType, startId: Int, raw: String): List<MediaEntity> =
        raw.trimIndent().lines().filter { it.isNotBlank() }.mapIndexed { i, line ->
            val f = line.split("|").map { it.trim() }
            val id = startId + i
            val rar = when (f[6]) { "L" -> Raridade.LENDARIO; "E" -> Raridade.EPICO; "R" -> Raridade.RARO; else -> Raridade.COMUM }
            MediaEntity(
                id = id, title = f[0], type = type.name, synopsis = f[7],
                coverUrl = "https://picsum.photos/seed/kagura$id/400/600",
                rarity = rar.name, status = initialStatus(id).name, userRating = null,
                score = f[5].toFloat(), vibe = f[3], tears = f[4].toInt(), units = f[2].toInt(),
                year = f[1].toInt(), accent = accents[id % accents.size],
                genre = f[3], genres = f[3]
            )
        }

    fun items(): List<MediaEntity> =
        parse(MediaType.ANIME, 1, ANIME) + parse(MediaType.MANGA, 101, MANGA)

    private const val ANIME = """
Fullmetal Alchemist: Brotherhood|2009|64|Aventura|4|9.1|L|Dois irmãos buscam a Pedra Filosofal para recuperar o que perderam.
Steins;Gate|2011|24|Sombrio|4|9.0|E|Um grupo descobre como enviar mensagens ao passado e paga o preço.
Cowboy Bebop|1998|26|Épico|3|8.9|E|Caçadores de recompensa vagam pelo espaço fugindo de seus passados.
Your Lie in April|2014|22|Romance|5|8.7|R|Um pianista bloqueado reencontra a música ao conhecer uma violinista livre.
Clannad: After Story|2008|24|Cotidiano|5|9.0|E|A vida adulta, a família e as perdas depois da escola.
Death Note|2006|37|Sombrio|2|8.6|R|Um estudante encontra um caderno capaz de matar e se acha um deus.
Hunter x Hunter|2011|148|Aventura|3|9.0|R|Gon parte em busca do pai e se torna um Hunter.
Violet Evergarden|2018|13|Romance|5|8.5|C|Uma ex-soldada aprende o que é amar escrevendo cartas para os outros.
Gintama|2006|367|Comédia|3|8.9|C|Samurais desempregados fazem bicos absurdos em uma Edo tomada por alienígenas.
Mob Psycho 100|2016|37|Comédia|3|8.6|C|Um garoto com poderes psíquicos tenta levar uma vida normal.
Attack on Titan|2013|87|Épico|3|9.0|R|A humanidade vive atrás de muralhas para escapar de gigantes devoradores.
Spy x Family|2022|37|Comédia|2|8.5|C|Espião, assassina e telepata fingem ser uma família comum.
Frieren|2023|28|Cotidiano|5|9.3|L|Uma maga elfa revisita a jornada do herói depois da morte do grupo.
Haikyuu!!|2014|85|Aventura|3|8.7|C|Um jogador baixo sonha em voar nas quadras de vôlei.
March Comes in Like a Lion|2016|44|Cotidiano|4|8.4|C|Um jovem prodígio do shogi enfrenta a solidão.
Made in Abyss|2017|13|Sombrio|4|8.7|R|Uma garota desce a um abismo misterioso atrás da mãe.
"""

    private const val MANGA = """
Berserk|1989|364|Sombrio|4|9.4|L|Um mercenário marcado luta contra o destino e contra os demônios que o cercam.
Vagabond|1998|327|Épico|3|9.2|E|A jornada de um espadachim em busca de ser o maior do Japão feudal.
One Piece|1997|1120|Aventura|4|9.2|R|Luffy reúne uma tripulação para encontrar o tesouro supremo dos mares.
Monster|1994|162|Sombrio|3|9.1|E|Um cirurgião caça o paciente que salvou e que virou um assassino.
Vinland Saga|2005|209|Épico|4|9.0|R|Um jovem viking descobre o custo da vingança e busca um lar sem guerra.
20th Century Boys|1999|249|Sombrio|3|8.9|C|Amigos de infância enfrentam um culto que nasceu de brincadeiras antigas.
Slam Dunk|1990|276|Aventura|4|9.0|R|Um delinquente aprende basquete para impressionar a garota que ama.
Yotsuba&!|2003|110|Comédia|1|8.8|C|Uma menina curiosa transforma cada dia comum em uma grande descoberta.
Chainsaw Man|2018|97|Sombrio|2|8.6|C|Denji vira caçador de demônios após se fundir com seu cão-motosserra.
Blue Period|2017|80|Cotidiano|3|8.5|C|Um estudante exemplar encontra na pintura uma razão para se esforçar.
Nana|2000|84|Romance|5|8.6|C|Duas jovens com o mesmo nome se encontram em Tóquio e dividem apartamento.
Fruits Basket|1998|136|Romance|5|8.8|R|Uma órfã descobre a família que se transforma em animais do zodíaco.
Oyasumi Punpun|2007|147|Sombrio|5|8.9|C|Um garoto retratado como um pássaro cresce entre as dores da vida.
Horimiya|2011|125|Romance|2|8.4|C|Dois colegas escondem seus lados secretos e se aproximam aos poucos.
Kaguya-sama: Love Is War|2015|281|Comédia|2|8.9|C|Dois gênios travam batalhas mentais para que o outro confesse o amor primeiro.
Skip and Loafer|2018|60|Cotidiano|2|8.4|C|Uma garota do interior vive a nova rotina em uma escola de Tóquio.
"""
}
