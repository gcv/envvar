# envvar

A Clojure library designed to make working with environment variables easier. It is based on (and borrows from [environ](https://github.com/weavejester/environ), but is friendlier for using non-checked-in user-specific `.env` files (similar to the [Ruby dotenv gem](https://github.com/bkeepers/dotenv)), and allows simple augmentation of environment variable values. It does not support `.lein-env` integration.


## Usage

Envvar resolves variable sources in the following order:

1. Environment variables.
2. Java system properties.
3. Values in the `.env` file in the project (a Java properties file).
4. Anything in user-loaded Java properties files.
5. Dynamic overrides.

    (require '[envvar.core :as e])
    ;; return all environment variables as a map:
    e/@*env*
    ;; return the value of the PATH environment variable:
    (:path e/@*env*)
    ;; augment the environment:
    (e/with-env [:one "hello", :two "world"]
      ;; e/@*env* will now also contain dynamically-scoped bindings for :one and :two
    )
    ;; load more properties into the environment
    (e/assoc-file-variables! "/path/to/properties.file")
    ;; reset all loaded properties, including ones from the .env file
    (e/reset-file-variables!)


## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
