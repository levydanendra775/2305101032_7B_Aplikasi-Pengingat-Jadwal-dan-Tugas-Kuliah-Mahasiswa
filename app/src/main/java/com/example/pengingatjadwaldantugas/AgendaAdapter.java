package com.example.pengingatjadwaldantugas;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.VH> {

    public interface Listener {
        void onToggle(Agenda a, boolean selesai);
        void onEdit(Agenda a);
        void onHapus(Agenda a);
    }

    private final List<Agenda> data = new ArrayList<>();
    private final Listener listener;
    private final SimpleDateFormat fmt =
            new SimpleDateFormat("EEE, d MMM · HH:mm", new Locale("id", "ID"));

    public AgendaAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<Agenda> baru) {
        data.clear();
        data.addAll(baru);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agenda, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Agenda a = data.get(position);
        Context c = h.itemView.getContext();

        boolean tugas = Agenda.TUGAS.equals(a.getJenis());
        h.tvJenis.setText(a.getJenis());
        h.tvJenis.setBackgroundResource(tugas ? R.drawable.bg_chip_tugas : R.drawable.bg_chip_jadwal);
        h.tvJenis.setTextColor(ContextCompat.getColor(c, tugas ? R.color.chip_tugas_teks : R.color.biru));

        boolean terlewat = !a.isSelesai() && a.getWaktu() < System.currentTimeMillis();
        String waktu = fmt.format(new Date(a.getWaktu()));
        h.tvWaktu.setText(terlewat ? waktu + " · Terlewat" : waktu);
        h.tvWaktu.setTextColor(ContextCompat.getColor(c, terlewat ? R.color.merah : R.color.tinta_lembut));

        h.tvJudul.setText(a.getJudul());
        int flags = h.tvJudul.getPaintFlags();
        h.tvJudul.setPaintFlags(a.isSelesai()
                ? flags | Paint.STRIKE_THRU_TEXT_FLAG
                : flags & ~Paint.STRIKE_THRU_TEXT_FLAG);

        if (a.getCatatan() == null || a.getCatatan().isEmpty()) {
            h.tvCatatan.setVisibility(View.GONE);
        } else {
            h.tvCatatan.setVisibility(View.VISIBLE);
            h.tvCatatan.setText(a.getCatatan());
        }

        h.tvPengingat.setText(Agenda.labelPengingat(a.getPengingat()));
        h.itemView.setAlpha(a.isSelesai() ? 0.55f : 1f);

        h.cbSelesai.setOnCheckedChangeListener(null);
        h.cbSelesai.setChecked(a.isSelesai());
        h.cbSelesai.setOnCheckedChangeListener((btn, checked) -> listener.onToggle(a, checked));

        h.itemView.setOnClickListener(v -> listener.onEdit(a));
        h.btnHapus.setOnClickListener(v -> listener.onHapus(a));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        CheckBox cbSelesai;
        TextView tvJenis, tvWaktu, tvJudul, tvCatatan, tvPengingat;
        ImageButton btnHapus;

        VH(@NonNull View v) {
            super(v);
            cbSelesai = v.findViewById(R.id.cbSelesai);
            tvJenis = v.findViewById(R.id.tvJenis);
            tvWaktu = v.findViewById(R.id.tvWaktu);
            tvJudul = v.findViewById(R.id.tvJudul);
            tvCatatan = v.findViewById(R.id.tvCatatan);
            tvPengingat = v.findViewById(R.id.tvPengingat);
            btnHapus = v.findViewById(R.id.btnHapus);
        }
    }
}