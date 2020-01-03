if [ -z $1 ]
then
  echo "please need to specify a build target (dev, prod, etc)"
  exit 1
fi

echo "== BUILDING ${1} =="

mkdir -p frontend-target/$1/public/js
rm -rf frontend-target/$1/public/js/*

echo "=> compiling sass"
sass sass/main.scss frontend-target/$1/public/stylesheets/main.css || { echo "sass failed"; exit 1; }
echo "=> finished building sass"

echo "=> building cljs"
npx shadow-cljs release $1 || { echo "shadow-cljs build failed"; exit 1; }
echo "=> finished building cljs"

echo "=> building uberjar"
lein with-profiles $1 uberjar || { echo "lein uberjar build failed"; exit 1; }
echo "=> finished building uberjar"

mkdir -p target/build
cp target/uberjar/*-standalone.jar target/build/app.jar
chmod 755 target/build/app.jar
