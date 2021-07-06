import validUrl from "valid-url"; // or this

export default class Utils {

  /**
   * Method to Check if the provided url is valid or not 
   * @param {*} url 
   */
  isValidUrl(url) {
    let result= url.match(/(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g);
    if(result){
      console.log("url "+url+""+validUrl.isUri(url));
      return true;
    } else {
      return false;
    }
  }
}
