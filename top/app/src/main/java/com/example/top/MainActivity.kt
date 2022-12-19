package com.example.top

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.top.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArtistAdapter

    // TODO: extract commons
    // TODO: refresh artist order on remove
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configToolbar()
        configAdapter()
        configRecyclerView()
        binding.fab.setOnClickListener(::onAddArtistClicked)
        configDatabase()

//        generateDefaultArtists()
    }

    private fun configToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun configAdapter() {
        adapter = ArtistAdapter(this)
    }

    private fun configRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun configDatabase() {
        val artistDao = AppDatabase.getDatabase(this).artistDao()
        ArtistRepository.setDao(artistDao)
    }

    private fun generateDefaultArtists() {
        // TODO: move to another class
        val names = arrayOf("Rachel", "Mary Elizabeth", "Jessica", "Gal")
        val surnames = arrayOf("McAdams", "Winstead", "Chastain", "Gadot")
        val birthDates = longArrayOf(280108800000L, 470469600000L, 228031200000L, 483667200000L)
        val birthPlaces = arrayOf("Canada", "USA", "USA", "Israel")
        val heights = shortArrayOf(163, 173, 163, 178)
        val notes = arrayOf(
            "Rachel Anne McAdams was born on November 17, 1978 in London, Ontario, Canada, to Sandra Kay (Gale), a nurse, and Lance Frederick McAdams, a truck driver and furniture mover. She is of English, Welsh, Irish, and Scottish descent. Rachel became involved with acting as a teenager and by the age of 13 was performing in Shakespearean productions in summer theater camp; she went on to graduate with honors with a BFA degree in Theater from York University. After her debut in an episode of Disney's The Famous Jett Jackson (1998), she co-starred in the Canadian TV series Slings and Arrows (2003), a comedy-drama about the trials and travails of a Shakespearean theater group, and won a Gemini award for her performance in 2003.\nHer breakout role as Regina George in the hit comedy Chicas pesadas (2004) instantly catapulted her onto the short list of Hollywood's hottest young actresses. She followed that film with a star turn opposite Ryan Gosling in the adaptation of the Nicholas Sparks bestseller Diario de una pasión (2004), which was a surprise box office success and became the predominant romantic drama for a new, young generation of moviegoers. After filming, McAdams and Gosling became romantically involved and dated through mid-2007. McAdams next showcased her versatility onscreen with the manic comedy Los cazanovias (2005), the thriller Vuelo nocturno (2005), and the holiday drama The Family Stone (2005).\nMcAdams then explored the independent film world with Infieles (2007), which premiered at the Toronto Film Festival and also starred Pierce Brosnan, Chris Cooper and Patricia Clarkson. Starring roles in the military drama The Lucky Ones (2008), the newspaper thriller Los secretos del poder (2009), and the romance Te amaré por siempre (2009) followed before she starred opposite Robert Downey Jr. and Jude Law in Guy Ritchie's international blockbuster Sherlock Holmes (2009). McAdams played the plucky producer of a failing morning TV show in Morning Glory (2010), the materialistic fiancée of Owen Wilson in Woody Allen's Medianoche en París (2011), and returned to romantic drama territory with the hit film Votos de amor (2012) opposite Channing Tatum. The actress also stars with Ben Affleck in Terrence Malick's Deberás amar (2012) and alongside Noomi Rapace in Brian De Palma's thriller Pasión, un asesinato perfecto (2012).\nIn 2005, McAdams received ShoWest's \"Supporting Actress of the Year\" Award as well as the \"Breakthrough Actress of the Year\" at the Hollywood Film Awards. In 2009, she was awarded with ShoWest's \"Female Star of the Year.\" As of 2011, she has been romantically linked with her Medianoche en París (2011) co-star Michael Sheen.",
            "Mary Elizabeth Winstead is a gifted actress, known for her versatile work in a variety of film and television projects. Possibly most known for her role as Ramona Flowers in Scott Pilgrim contra el mundo (2010), she has also starred in critically acclaimed independent films such as Tocando fondo (2012), for which she received an Independent Spirit Award nomination, as well as genre fare like Destino final 3 (2006) and Quentin Tarantino's Death Proof (2007).",
            "Jessica Michelle Chastain was born in Sacramento, California, and was raised in a middle-class household in a northern California suburb. Her mother, Jerri Chastain, is a vegan chef whose family is originally from Kansas, and her stepfather is a fireman. She discovered dance at the age of nine and was in a dance troupe by age thirteen. She began performing in Shakespearean productions all over the Bay area.",
            "Gal Gadot is an Israeli actress, singer, martial artist, and model. She was born in Rosh Ha'ayin, Israel, to an Ashkenazi Jewish family. Her parents are Irit, a teacher, and Michael, an engineer, who is a sixth-generation Israeli. She served in the IDF for two years, and won the Miss Israel title in 2004.\nGal began modeling in the late 2000s, and made her film debut in the fourth film of the Fast and Furious franchise, Rápidos y furiosos (2009), as Gisele. Her role was expanded in the sequels Rápidos y Furiosos: 5in control (2011) and Rápidos y furiosos 6 (2013), in which her character was romantically linked to Han Seoul-Oh (Sung Kang). In the films, Gal performed her own stunts. She also appeared in the 2010 films Una noche fuera de serie (2010) and Knight and Day (2010).\nIn early December 2013, Gal was cast as Wonder Woman in the superhero team-up film Batman vs. Superman: El origen de la justicia (2016), and filming began in 2014 for a March 2016 release. Gadot received swordsmanship, Kung Fu kickboxing, Capoeira and Brazilian Jiu-Jitsu training in preparation for the role. As a result, her performance as the superhero, which is the first time for the character on film, was hailed as one of the best parts of the otherwise poorly-received film. The film is part of the DC Extended Universe, and Gadot plays the role again in the solo film Mujer Maravilla (2017), which was received very positively, and superhero team-up Justice League (2017).\nGal is a motorcycle enthusiast, and owns a black 2006 Ducati Monster-S2R. She has been married to Israeli businessman Yaron Versano since September 28, 2008. They have two daughters."
        )
        val photoUrls = arrayOf(
            "https://imagenes.20minutos.es/files/og_thumbnail/uploads/imagenes/2021/06/14/rachel-mcadams.jpeg",
            "https://upload.wikimedia.org/wikipedia/commons/7/7b/Mary_Elizabeth_Winstead.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/SDCC_2015_-_Jessica_Chastain_%2819544181630%29.jpg/1024px-SDCC_2015_-_Jessica_Chastain_%2819544181630%29.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Gal_Gadot_%2835402074433%29.jpg/1024px-Gal_Gadot_%2835402074433%29.jpg"
        )

        for (i in 0..3) {
            val artist = Artist(
                0,
                names[i],
                surnames[i],
                birthDates[i],
                birthPlaces[i],
                heights[i],
                i + 1, notes[i],
                photoUrls[i]
            )
            adapter.add(artist)

            ArtistRepository.addArtist(artist)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.artistList = getArtistsFromDB()
        adapter.notifyDataSetChanged()
    }

    private fun getArtistsFromDB() = ArtistRepository.getAll().toMutableList()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(artist: Artist) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(Artist.ID, artist.id)
        startActivity(intent)
    }

    override fun onLongItemClick(artist: Artist) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(60)
        AlertDialog.Builder(this)
            .setTitle(R.string.main_dialog_delete_title)
            .setMessage(getString(R.string.main_dialog_delete_message, artist.name))
            .setPositiveButton(R.string.details_dialog_delete_delete) {_, _ ->
                ArtistRepository.delete(artist)
                adapter.remove(artist)
                showMessage(R.string.main_dialog_delete_success)
            }
            .setNegativeButton(R.string.label_dialog_cancel, null)
            .show()
    }

    private fun showMessage(message: Int) {
        Snackbar.make(binding.containerMain, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun onAddArtistClicked(view: View) {
        val intent = Intent(this, AddArtistActivity::class.java)
        intent.putExtra(Artist.ORDER, adapter.itemCount + 1)
        startActivityForResult(intent, 1)
    }

}