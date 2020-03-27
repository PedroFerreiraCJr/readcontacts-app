package br.com.dotofcodex.contacts_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.dotofcodex.contacts_app.R;
import br.com.dotofcodex.contacts_app.model.Contact;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Contact> data;

    public ContactRecyclerViewAdapter(Context context, List<Contact> data) {
        super();
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.activity_main_recyclerview_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.data.get(position));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView id;
        TextView name;
        TextView number;

        public ViewHolder(View view) {
            super(view);
            this.id = view.findViewById(R.id.tv_id);
            this.name = view.findViewById(R.id.tv_name);
            this.number = view.findViewById(R.id.tv_number);
        }

        public void bind(Contact contact) {
            this.id.setText(contact.getId());
            this.name.setText(contact.getName());
            this.number.setText(contact.getNumber());
        }
    }
}
