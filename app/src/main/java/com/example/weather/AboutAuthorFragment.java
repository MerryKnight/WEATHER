package com.example.weather;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AboutAuthorFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author, container, false);

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0f, 500f);
        animator.setDuration(1000); // Длительность анимации в миллисекундах
        animator.setRepeatCount(ObjectAnimator.INFINITE); // Бесконечное повторение
        animator.setRepeatMode(ObjectAnimator.REVERSE); // Возврат к началу после достижения конца
        animator.start();
        // Инициализация элементов интерфейса и привязка данных, если необходимо
        return view;
    }

}
