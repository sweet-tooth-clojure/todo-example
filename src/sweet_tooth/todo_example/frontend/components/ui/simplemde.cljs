(ns sweet-tooth.todo-example.frontend.components.ui.simplemde
  (:require ["react-simplemde-editor" :default SimpleMDE]
            [sweet-tooth.frontend.form.components :as stfc]))

(defmethod stfc/input-type-opts :simplemde
  [opts]
  (-> (stfc/input-type-opts-default opts)
      (dissoc :type)))

(defmethod stfc/input :simplemde
  [{:keys [partial-form-path attr-path value]}]
  [:> SimpleMDE {:onChange (fn [val] (stfc/dispatch-new-val partial-form-path attr-path val))
                 :value    value
                 :options  {:toolbar      ["bold" "italic" "quote" "unordered-list" "ordered-list"]
                            :autofocus    true
                            :status       false
                            :spellChecker false
                            :autoDownloadFontAwesome false}
                 :extraKeys {:Tab (fn [cm]
                                    (.focus (.querySelector (.closest (.getTextArea cm) "form")
                                                            "input[type=submit]")))}}])
