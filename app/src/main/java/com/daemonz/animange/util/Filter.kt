package com.daemonz.animange.util

enum class SortField(val value: String) {
    Time("_id"),
    LastUpdated("modified.time"),
    CreatedYear("year")
}

enum class TypeList(val value: String) {
    New("phim-moi"),
    Series("phim-bo"),
    Movie("phim-le"),
    TvShow("tv-shows"),
    Anime("hoat-hinh"),
    VietSub("phim-vietsub"),
    VietDub("phim-thuyet-minh"),
    VietFakeSub("phim-long-tieng"),
    OnGoing("phim-bo-dang-chieu"),
    Completed("phim-bo-hoan-thanh"),
    ComingSoon("phim-sap-chieu"),
    SubTeam("subteam")
}

enum class Category(val value: String) {
    All(""),
    Action("hanh-dong"),
    Romance("tinh-cam"),
    Comedy("hai-huoc"),
    CoTrang("co-trang"),
    TamLy("tam-ly"),
    HinhSu("hinh-su"),
    ChienTranh("chien-tranh"),
    TheThao("the-thao"),
    VoThuat("vo-thuat"),
    VienTuong("vien-tuong"),
    PhieuLuu("phieu-luu"),
    KhoaHoc("khoa-hoc"),
    KinhDi("kinh-di"),
    AmNhac("am-nhac"),
    ThanThoai("thanh-thoai"),
    TaiLieu("tai-lieu"),
    GiaDinh("gia-dinh"),
    ChinhKich("chinh-kich"),
    BiAn("bi-an"),
    HocDuong("hoc-duong"),
    KinhDien("kinh-dien"),
    Phim18("phim-18")
}
enum class Country(val value: String) {
    VietNam("viet-nam"),
    AuMy("au-my"),
    TrungQuoc("trung-quoc"),
    NhatBan("nhat-ban"),
    Anh("anh")
}
