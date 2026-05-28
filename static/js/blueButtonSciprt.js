let all = document.getElementsByClassName("blue-button");

for(const b of all){
    b.addEventListener('mousemove', (e)=>{
        // e = Mouse click event.
        var rect = e.target.getBoundingClientRect();
        var x = e.clientX - rect.left; //x position within the element.
        var y = e.clientY - rect.top;  //y position within the element.

        let normX = (x / rect.width) - 0.5; normX *= 2;
        let normY = (y / rect.height)  - 0.5; normY *= 2;

        let t = e.target;

        const mul = 25;
        t.style.transform = `rotateY(${normX * mul}deg) rotateX(${normY * mul * -1}deg) scale(1.2)`; // works
    });
    b.addEventListener('mouseout', (e)=>{
        let t = e.target;
        t.style.transform = "";
    });
}