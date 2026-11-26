package id.pamtirtakemuning.watermeter.data

import java.util.Calendar

/** Data resmi teknisi yang login (demo). */
val technician = User(
    name = "Andi Pratama",
    nip = "19920314 201803 1 007",
    username = "andi.pratama",
    phone = "0812-3456-7890",
    email = "andi.pratama@pamtirtakemuning.co.id",
    address = "Jl. Siliwangi No. 21, Kel. Kuningan, Kab. Kuningan",
    role = "Teknisi Lapangan",
    branch = "Cabang Kuningan",
)

private fun daysAgo(n: Int, h: Int = 8, m: Int = 0): Long = Calendar.getInstance().apply {
    add(Calendar.DAY_OF_YEAR, -n)
    set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, m); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

private const val B = "Cabang Kuningan"
private val O = technician.name

fun seedAssignments(): List<Assignment> = listOf(
    Assignment("a1", "PGM-2026-0125", "Budi Santoso", "32080100215", "6281234567801", "Jl. Raya Kuningan No. 25, Kel. Kuningan, Kec. Kuningan", B, "R1 / Rumah Tangga",
        JobType.Penggantian, "Water meter macet",
        "Angka stand tidak bergerak selama 2 bulan terakhir meskipun pelanggan tetap memakai air. Hasil catat meter tertahan di angka 01250 sehingga pemakaian tidak dapat ditagihkan dengan benar.",
        "WM-2023-00125", daysAgo(0), O, Status.Belum, "Meter berada di depan pagar. Pelanggan dapat ditemui setelah pukul 09.00.", -6.9759, 108.4838),
    Assignment("a2", "PGM-2026-0126", "Siti Rahmawati", "32080100342", "6281234567802", "Jl. Siliwangi No. 112, Kel. Cijoho, Kec. Kuningan", B, "R2 / Rumah Tangga Menengah",
        JobType.Perbaikan, "Indikasi kebocoran",
        "Terdapat rembesan air pada sambungan inlet water meter dan tagihan pelanggan naik sekitar 40% dibanding bulan lalu. Periksa sambungan, ganti seal, dan pasang ulang meter bila diperlukan.",
        "WM-2022-00873", daysAgo(0), O, Status.Belum, "Siapkan seal dan sok drat 1/2 inci.", -6.9811, 108.4790),
    Assignment("a3", "PGM-2026-0121", "Rumah Makan Sari Rasa", "32080200418", "6281234567803", "Jl. Ahmad Yani No. 7, Kel. Ancaran, Kec. Kuningan", B, "N1 / Niaga Kecil",
        JobType.Penggantian, "Kaca meter buram",
        "Kaca pelindung water meter buram dan berembun sehingga angka stand tidak dapat dibaca oleh petugas catat meter selama 3 periode berturut-turut.",
        "WM-2021-00561", daysAgo(1), O, Status.Proses, "Hubungi pemilik sebelum datang. Toko tutup pukul 21.00.", -6.9702, 108.4861,
        confirmation = Confirmation(kondisiFisik = KondisiFisikWmLama.KacaPecah, notes = "Kaca sangat buram, unit meter terpasang di box", at = daysAgo(0, 8, 40)),
        draft = Report(brand = "Itron", meterType = "Multi Jet", newSerial = "ITR-26-004471", standLast = "1284", standNow = "1311")),
    Assignment("a4", "PGM-2026-0122", "Dedi Kurniawan", "32080100577", "6281234567804", "Jl. Veteran Gg. Mawar No. 4, Kel. Windusengkahan", B, "R1 / Rumah Tangga",
        JobType.Penggantian, "Meter tidak terbaca",
        "Display dan register meter mati total akibat terendam air hujan. Water meter tidak dapat dibaca sama sekali dan perlu diganti dengan unit baru.",
        "WM-2022-00390", daysAgo(1), O, Status.Belum, "Display mati total.", -6.9850, 108.4915),
    Assignment("a5", "PGM-2026-0118", "SDN 2 Kuningan", "32080300102", "6281234567805", "Jl. Pendidikan No. 3, Kel. Kuningan, Kec. Kuningan", B, "S1 / Sosial Umum",
        JobType.Perbaikan, "Water meter rusak",
        "Body meter retak terkena benturan kendaraan proyek di depan sekolah. Air merembes dari body meter dan perlu penanganan segera.",
        "WM-2020-00214", daysAgo(2), O, Status.Belum, "Temui penjaga sekolah, Pak Joko.", -6.9778, 108.4802),
    Assignment("a6", "PGM-2026-0104", "Hj. Neneng Komariah", "32080100629", "6281234567806", "Jl. Cut Nyak Dien No. 18, Kel. Purwawinangun", B, "R2 / Rumah Tangga Menengah",
        JobType.Penggantian, "Water meter macet",
        "Angka stand tidak berubah selama 1 bulan. Pelanggan melaporkan aliran air normal namun tagihan minimum terus muncul.",
        "WM-2021-00718", daysAgo(5), O, Status.Selesai, "", -6.9690, 108.4770,
        confirmation = Confirmation(kondisiFisik = KondisiFisikWmLama.Macet, notes = "Mesin terhenti", at = daysAgo(3, 9, 5)),
        report = Report("Itron", "Multi Jet", "ITR-26-003902", "0942", "0968", "5", "SG-208841", "Normal", "Meter lama macet. Meter baru diuji tanpa kebocoran.", doneAt = daysAgo(3, 10, 20))),
    Assignment("a7", "PGM-2026-0099", "Asep Saepudin", "32080100710", "6281234567807", "Jl. Moh. Toha No. 60, Kel. Kasturi, Kec. Kuningan", B, "R1 / Rumah Tangga",
        JobType.Perbaikan, "Indikasi kebocoran",
        "Ditemukan genangan di sekitar box meter. Diduga kebocoran dari body meter dan sambungan pipa outlet.",
        "WM-2022-01044", daysAgo(9), O, Status.Selesai, "", -6.9832, 108.4743,
        confirmation = Confirmation(kondisiFisik = KondisiFisikWmLama.BodiKorosi, notes = "Body retak dan korosi", at = daysAgo(8, 13, 10)),
        report = Report("Sensus", "Single Jet", "SNS-26-001288", "0511", "0530", "5", "SG-208790", "Normal", "Body meter retak, sambungan diganti.", doneAt = daysAgo(8, 14, 45))),
    Assignment("a8", "PGM-2026-0071", "CV. Tirta Laundry", "32080200488", "6281234567808", "Jl. Raya Cirendang No. 41, Kec. Kuningan", B, "N2 / Niaga Menengah",
        JobType.Penggantian, "Kaca meter buram",
        "Kaca meter berlumut dan buram sehingga stand tidak terbaca. Pelanggan niaga dengan pemakaian tinggi sehingga pembacaan akurat sangat diperlukan.",
        "WM-2019-00832", daysAgo(2), O, Status.Validasi, "Pekerjaan selesai, menunggu validasi Pusat.", -6.9650, 108.4920,
        confirmation = Confirmation(kondisiFisik = KondisiFisikWmLama.BerembunParah, notes = "Kaca meter berembun dan berlumut", at = daysAgo(1, 8, 15)),
        report = Report("Zenner", "Volumetrik", "ZNR-26-000745", "3320", "3402", "6", "SG-208512", "Normal", "Kaca buram dan berlumut. Pelanggan menyetujui berita acara.", doneAt = daysAgo(1, 9, 50))),
    Assignment("a9", "PGM-2026-0130", "H. Ahmad Fauzi", "32080100889", "6281234567809", "Jl. Jend. Sudirman No. 88, Kel. Awirarangan, Kec. Kuningan", B, "R2 / Rumah Tangga Menengah",
        JobType.Perbaikan, "Laporan tidak sesuai",
        "Laporan pengerjaan sebelumnya ditolak oleh verifikator karena foto water meter baru kurang jelas dan nomor seri baru tidak sesuai dengan fisik unit.",
        "WM-2022-00512", daysAgo(1), O, Status.Ditolak, "Perbarui data dan foto perbaikan water meter.", -6.9740, 108.4820,
        confirmation = Confirmation(kondisiFisik = KondisiFisikWmLama.Normal, notes = "Meter lama bocor di inlet", at = daysAgo(1, 9, 30)),
        report = Report("Itron", "Multi Jet", "ITR-26-009988", "1020", "1045", "5", "SG-208999", "Normal", "Perbaikan telah dilakukan.", doneAt = daysAgo(1, 11, 0)),
        rejectionReason = "Foto water meter baru kurang jelas dan nomor seri baru (ITR-26-009988) tidak cocok dengan fisik unit. Harap perbarui data dan foto perbaikan."),
)
