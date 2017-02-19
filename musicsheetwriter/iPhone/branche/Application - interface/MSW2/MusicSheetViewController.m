//
//  MusicSheetViewController.m
//  MSW
//
//  Created by Antoine Simon on 19/02/2016.
//  Copyright © 2016 Score Lab. All rights reserved.
//

#import "MusicSheetViewController.h"

@interface MusicSheetViewController ()

@end
//pourquoi ne pas tourner que l'image quand l'appareil s'incline?!

@implementation MusicSheetViewController
/*
 example of how to fill the object: musicSheet.musicSheetModal = @[self.titleMusicSheet[row], self.urlMusicSheet[row], self.userMusicSheet[row]];
 */

- (void)viewDidLoad {
    [super viewDidLoad];
    //check if it is a guest mode
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuestId:self] == true) {
        return;
    }
    
    self.scrollView.minimumZoomScale=0.5;
    self.scrollView.maximumZoomScale=6.0;
    self.scrollView.contentSize=CGSizeMake(1280, 960);
    self.scrollView.delegate=self;
    
    UrlMusicSheet = self.musicSheetModal[1];
    Title = self.musicSheetModal[0];
    user = self.musicSheetModal[2];
    fid = self.musicSheetModal[3];
    
    NSString *post = [NSString stringWithFormat:@"Titre: %@ - Par: %@", Title, user];
    self.prompt.text = post;
    [self refreshStateButton];
    [self DisplayMusicSheet];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView{
    return self.musicSheet;
}

-(void)DisplayMusicSheet{
    self.musicSheet.image = [UIImage imageWithData: [NSData dataWithContentsOfURL:[NSURL URLWithString:UrlMusicSheet]]];
}

-(BOOL) checkSheetInTheList{
    int i = 0;
    NSString * post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/scores/favourites", Id_global];
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:post];

    if (code_global != 200){
        [api popup:dict1 id:self];
        return FALSE;
    }
    _idRowCounter = [[NSMutableArray alloc] initWithCapacity:0];
    for(NSDictionary *item in dict1) {
        [_idRowCounter addObject:(NSString *)[item valueForKey:@"id"]];
    }
    NSString *tmp;
    while (i < _idRowCounter.count)
    {
        tmp = [NSString stringWithFormat:@"%@", self.idRowCounter[i]];
        if ([tmp isEqualToString:[NSString stringWithFormat:@"%@", self.musicSheetModal[3]]]){
            return TRUE;
        }
        i = i + 1;
    }
    return FALSE;
}

-(void) refreshStateButton{
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuestId:self] != true) {
        if (([self.heart imageForState:UIControlStateNormal] != [UIImage imageNamed:@"favorite.png"]) && ([self checkSheetInTheList])){
            [self.heart setImage:[UIImage imageNamed:@"favorite.png"] forState:UIControlStateNormal];
        }
        else{
            [self.heart setImage:[UIImage imageNamed:@"favorite-2.png"] forState:UIControlStateNormal];
        }
    }
    else{
        [self.heart setImage:[UIImage imageNamed:@"favorite-2.png"] forState:UIControlStateNormal];
    }
}

- (IBAction)like:(id)sender {
    if (![self checkSheetInTheList]){
        NSString * post1 =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/scores/favourites", Id_global];
        ApiMethod *api = [[ApiMethod alloc]init];
        NSMutableDictionary *parametersDictionary = [[NSMutableDictionary alloc]initWithCapacity:0];
        [parametersDictionary setValue:fid forKey:@"id"];
        NSDictionary *dict1 = [api ApiMethodPostAfnetworkingAt:post1 Data:parametersDictionary];
        if (code_global != 200){
            [api popup:dict1 id:self];
            return;
        }
        [self refreshStateButton];
        [self viewDidLoad];
    }
    else{
        NSString * post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/scores/favourites/%@",Id_global , fid];
        ApiMethod *api = [[ApiMethod alloc]init];
        NSDictionary *dict1 = [api ApiMethodDeleteAfnetworkingAt:post];
        if (code_global != 204){
            [api popup:dict1 id:self];
            return;
        }
        [self refreshStateButton];
        [self viewDidLoad];
    }
}

@end
