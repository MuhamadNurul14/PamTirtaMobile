package id.pamtirtakemuning.watermeter.data

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

data class PenugasanListResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<PenugasanDto>
)

data class PenggantianWmListResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val dataWrapper: PenggantianWmDataWrapper
)

data class PenggantianWmDataWrapper(
    @SerializedName("data") val data: List<PenggantianWmItemDto>
)

data class ApiResponse<T>(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T
)

data class PenugasanDto(
    @SerializedName("id_penugasan") val idPenugasan: String,
    @SerializedName("nomor_spk") val nomorSpk: String,
    @SerializedName("alasan") val alasan: String?,
    @SerializedName("tgl_penugasan") val tglPenugasan: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("pelanggan") val pelanggan: PelangganDto?,
    @SerializedName("unit") val unit: UnitDto?,
    @SerializedName("teknisi") val teknisi: TeknisiDto?
)

data class PelangganDto(
    @SerializedName("id_pelanggan_pdam") val idPelangganPdam: String?,
    @SerializedName("nama") val nama: String?,
    @SerializedName("alamat") val alamat: String?,
    @SerializedName("tarif") val tarif: TarifDto?
)

data class TarifDto(
    @SerializedName("nama_tarif") val namaTarif: String?
)

data class UnitDto(
    @SerializedName("nama_unit") val namaUnit: String?
)

data class TeknisiDto(
    @SerializedName("fullname") val fullname: String?
)

data class PenggantianWmItemDto(
    @SerializedName("id_penggantian") val idPenggantian: String,
    @SerializedName("id_penugasan") val idPenugasan: String,
    @SerializedName("no_seri_wm_lama") val noSeriWmLama: String?,
    @SerializedName("stand_wm_lama") val standWmLama: Any?,
    @SerializedName("kondisi_fisik_wm_lama") val kondisiFisikWmLama: String?,
    @SerializedName("path_image_wm_lama") val pathImageWmLama: String?,
    @SerializedName("no_seri_wm_baru") val noSeriWmBaru: String?,
    @SerializedName("stand_awal_wm_baru") val standAwalWmBaru: Any?,
    @SerializedName("path_image_wm_baru") val pathImageWmBaru: String?,
    @SerializedName("tgl_penggantian") val tglPenggantian: String?,
    @SerializedName("id_merk") val idMerk: String?,
    @SerializedName("tahap_penggantian") val tahapPenggantian: String?,
    @SerializedName("status_validasi") val statusValidasi: String?,
    @SerializedName("catatan_validasi") val catatanValidasi: String?,
    @SerializedName("penugasan") val penugasan: PenugasanDto?,
    @SerializedName("merk") val merk: MerkDto?
)

data class MerkDto(
    @SerializedName("merk_wm") val merkWm: String?
)

interface ApiService {

    @GET("penugasan")
    suspend fun getPenugasan(): PenugasanListResponse

    @GET("penggantian-wm")
    suspend fun getPenggantianWm(): PenggantianWmListResponse

    @Multipart
    @POST("penggantian-wm/{id_penugasan}/data-wm-lama")
    suspend fun postDataWmLama(
        @Path("id_penugasan") idPenugasan: String,
        @Part("no_seri_wm_lama") noSeriWmLama: RequestBody,
        @Part("stand_wm_lama") standWmLama: RequestBody,
        @Part("kondisi_fisik_wm_lama") kondisiFisikWmLama: RequestBody,
        @Part imageWmLama: MultipartBody.Part
    ): Response<ApiResponse<PenggantianWmItemDto>>

    @Multipart
    @POST("penggantian-wm/{id_penugasan}/selesaikan")
    suspend fun postSelesaikan(
        @Path("id_penugasan") idPenugasan: String,
        @Part("no_seri_wm_baru") noSeriWmBaru: RequestBody,
        @Part("stand_awal_wm_baru") standAwalWmBaru: RequestBody,
        @Part("tgl_penggantian") tglPenggantian: RequestBody,
        @Part("id_merk") idMerk: RequestBody,
        @Part imageWmBaru: MultipartBody.Part
    ): Response<ApiResponse<PenggantianWmItemDto>>

    @Multipart
    @PATCH("penggantian-wm/{id_penugasan}/perbaiki")
    suspend fun patchPerbaiki(
        @Path("id_penugasan") idPenugasan: String,
        @Part("no_seri_wm_baru") noSeriWmBaru: RequestBody,
        @Part("stand_awal_wm_baru") standAwalWmBaru: RequestBody,
        @Part("tgl_penggantian") tglPenggantian: RequestBody,
        @Part("id_merk") idMerk: RequestBody,
        @Part imageWmBaru: MultipartBody.Part
    ): Response<ApiResponse<PenggantianWmItemDto>>

    @GET("user/profile")
    suspend fun getUserProfile(): User
}
