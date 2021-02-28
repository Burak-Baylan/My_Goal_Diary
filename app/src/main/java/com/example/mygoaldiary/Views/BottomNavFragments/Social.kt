package com.example.mygoaldiary.Views.BottomNavFragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.example.mygoaldiary.Creators.BottomSheets.AddPostSheet
import com.example.mygoaldiary.FirebaseManage.FirebaseAuthClass
import com.example.mygoaldiary.Helpers.SocialHelpers.GetPosts
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.Views.MainActivity
import com.example.mygoaldiary.R
import com.example.mygoaldiary.RecyclerView.SocialRecyclerViewAdapter
import com.example.mygoaldiary.databinding.FragmentSocialBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

open class Social : Fragment() {

    companion object{
        private lateinit var adapter : SocialRecyclerViewAdapter
        private lateinit var items : ArrayList<SocialModel>

        var _binding : FragmentSocialBinding? = null
        val binding get() = _binding!!
    }

    private lateinit var addPostSheetView : View
    private lateinit var addPostSheet : AddPostSheet
    private lateinit var firebaseAuthClass : FirebaseAuthClass

    private val auth = FirebaseAuth.getInstance()

    private var currentUser : FirebaseUser? = null

    override fun onResume() {
        currentUser = auth.currentUser
        currentUser?.let {
            binding.showUsernameTextView.text = currentUser!!.displayName
            currentUser!!.photoUrl?.let{
                Picasso.get().load(it).into(binding.socialPpIv)
            }
        }
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuthClass = FirebaseAuthClass(requireContext(), requireActivity())

        addPostSheet = AddPostSheet(requireContext(), requireActivity())
        addPostSheetView = addPostSheet.createSheet()
        binding.goToAddPost.setOnClickListener {
            addPostSheetView = addPostSheet.createSheet()
            addPostSheet.show()
        }

        val getPost = GetPosts()
        getPost.recyclerView = binding.socialRecyclerView
        getPost.loadingProgress = binding.postsLoadingProgress

        with(binding.socialRefreshLayout){
            this.setColorSchemeColors(Color.parseColor("#FFFFFF"))
            this.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))
            this.setOnRefreshListener {
                binding.socialRecyclerView.removeAllViews()
                items.clear()
                getPost.get(items, firebase.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING))
                this.isRefreshing = false
                adapter.notifyDataSetChanged()
            }
        }



        with(addPostSheetView.findViewById(R.id.categorySpinner) as SmartMaterialSpinner<*>){
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        backPressListener()
        initializeRecyclerView()
        getPost.get(items, firebase.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING))

        //getData()

        return view
    }

    private fun initializeRecyclerView() {
        items = ArrayList()
        adapter = SocialRecyclerViewAdapter(items, requireActivity())
        binding.socialRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.socialRecyclerView.adapter = adapter
    }

    private val firebase = FirebaseFirestore.getInstance()
    private fun getData() {
        /**
         * Filtreleme işlemi whereEqualTo ile yapılacak ve filtre sharedPreferences'e kaydedilecek.
         * Her girişte kullanıcının seçtiği filtre ile alakalı paylaşımlar gösterilecek.
         * Kullanıcı isterse beğeni sayısına göre, paylaşım tarihine göre, veya yorum sayısına göre paylaşımları görebilecek.
         *
         * Yapılacaklar:
         * 1-) Paylaşım yapılacak ekran oluşturulacak. !
         * 2-) Paylaşımları göster. !
         * 3-) Paylaşım yapılan kışının resmine tıklandığında profiline atması gerekecek ve eğer paylaşım yapan kişi profilini açık yaptıysa profil gözükecek.
         * 4-) Like işlemi doğru şekilde çalıştırılacak. !
         * 5-) Paylaşımı kaydetme işlemi yapılacak ve kullanıcı profilinden kaydettiği paylaşımlara ulaşacak.
         * 6-) Atılan post'un text'i paylaşılabilecek.
         * 7-) Paylaşım yapan kişi bloklanabilecek ve tabiki bir daha bloklanan kişinin paylaşımları gösterilmeyecek. (Ayrıca kullanıcının bloklanan kişileri görebileceği bir alan olabilir!)
         * *-) Atılan postun text'i üzerine uzun tıklanımda panoya kopyalansın.
         */

        firebase.collection("Users").document(firebaseAuthClass.getCurrentUser()!!.uid).collection("Marks").get().addOnSuccessListener { value1 ->
            if (value1 != null) {
                println("boş değil like")

                val documents1 = value1.documents

                var category : String
                var comment : String
                var currentDate : String
                var currentTime : String
                var ownerUuid : String
                var timeStamp : Timestamp
                var postId : String

                for (docs in documents1){
                    println("forda güzel: ${docs["markedPost"]}")

                    items.add(SocialModel("wuFu4cIMyINse4B6KOvBeEtOth73", "Games", "sg az kaldk the morning", "15/02/2021", "21:34:17", Timestamp.now(), "56c380c8-d644-42a7-89dc-ab2aef8c6dfd"))

                    /*firebase.collection("Posts").document(docs["markedPost"] as String).get().addOnSuccessListener { value2 ->
                        if (value2 != null){
                            println("valu 2 boş değil")
                            category = value2.get("category") as String
                            comment = value2.get("comment") as String
                            currentDate = value2.get("currentDate") as String
                            currentTime = value2.get("currentTime") as String
                            ownerUuid = value2.get("ownerUuid") as String
                            timeStamp = value2.get("timeStamp") as Timestamp
                            postId = value2.get("postId") as String

                            println("GELDİK: category: $category / comment: $comment / currentDate: $currentDate / currentTime: $currentTime / ownerUuid: $ownerUuid / timeStamp: $timeStamp / postId: $postId")

                            items.add(SocialModel(ownerUuid, category, comment, currentDate, currentTime, timeStamp, postId))
                        }else{
                            println("valu 2 boş")
                        }
                    }.addOnFailureListener {
                        println("FAY FAY FAY FAYIL")
                    }*/
                }
            }else{
                println("boş like")
            }
        }.addOnFailureListener {
            println("hata like: ${it.localizedMessage!!}")
        }
    }

    fun makeCurrentFragment(fragment : Fragment) =
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }

    protected open fun backPressListener() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                makeCurrentFragment(Home())
                MainActivity.BOTTOM_NAV.selectedItemId = R.id.homeFromMenu
            }
        })
    }
}